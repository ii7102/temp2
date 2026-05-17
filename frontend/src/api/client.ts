import type {
  AdminDashboardResponse,
  BillingOverview,
  DashboardResponse,
  LandingResponse,
  PaginatedUsersResponse,
  ProfileResponse,
  PublicSessionSummary,
  StudioDetailResponse,
  PaymentRow,
} from './types'
import { getAccessToken } from '../auth/tokenStore'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || '/api'

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(getAccessToken() ? { Authorization: `Bearer ${getAccessToken()}` } : {}),
      ...(init.headers ?? {}),
    },
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || response.statusText)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export const api = {
  getLanding: () => request<LandingResponse>('/public/landing'),
  getStudios: (params: Record<string, string | number | undefined>) => {
    const query = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== '') query.set(key, String(value))
    })
    return request<StudioDetailResponse['studio'][]>(`/public/studios?${query.toString()}`)
  },
  getStudio: (slug: string) => request<StudioDetailResponse>(`/public/studios/${slug}`),
  getSessions: (params: Record<string, string | number | undefined>) => {
    const query = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== '') query.set(key, String(value))
    })
    return request<PublicSessionSummary[]>(`/public/sessions?${query.toString()}`)
  },
  getMeProfile: () => request<ProfileResponse>('/me/profile'),
  updateMeProfile: (payload: Record<string, unknown>) =>
    request<ProfileResponse>('/me/profile', { method: 'PUT', body: JSON.stringify(payload) }),
  getDashboard: () => request<DashboardResponse>('/me/dashboard'),
  getBilling: () => request<BillingOverview>('/me/billing'),
  getBookings: () => request<any[]>('/me/bookings'),
  createSessionCheckout: (sessionId: string) => request<{ checkoutSessionId: string; url: string }>(`/me/bookings/${sessionId}/checkout`, { method: 'POST' }),
  createSubscriptionCheckout: () => request<{ checkoutSessionId: string; url: string }>('/me/subscription/checkout', { method: 'POST' }),
  createPortal: () => request<{ url: string }>('/me/subscription/portal', { method: 'POST' }),
  cancelSubscription: () => request<void>('/me/subscription', { method: 'DELETE' }),
  cancelBooking: (bookingId: string, reason: string) => request<void>(`/me/bookings/${bookingId}/cancel`, { method: 'POST', body: JSON.stringify({ reason }) }),
  createReview: (payload: { bookingId: string; rating: number; comment: string }) => request('/me/reviews', { method: 'POST', body: JSON.stringify(payload) }),
  getAdminDashboard: () => request<AdminDashboardResponse>('/admin/dashboard'),
  getAdminUsers: (query: string, page = 0, size = 20) => {
    const search = new URLSearchParams({ query, page: String(page), size: String(size) })
    return request<PaginatedUsersResponse>(`/admin/users?${search.toString()}`)
  },
  updateAdminRole: (keycloakUserId: string, role: 'user' | 'admin') =>
    request<void>(`/admin/users/role?keycloakUserId=${encodeURIComponent(keycloakUserId)}`, {
      method: 'POST',
      body: JSON.stringify({ role }),
    }),
  getPayments: () => request<PaymentRow[]>('/admin/payments'),
  refundPayment: (paymentId: string) => request<{ paymentId: string; status: string }>(`/admin/payments/refund?paymentId=${encodeURIComponent(paymentId)}`, { method: 'POST' }),
}

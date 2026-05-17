export type PublicStudioSummary = {
  id: string
  name: string
  slug: string
  neighborhood: string
  discipline: string
  description: string
  priceFromEur: string
  rating: string
  reviewCount: number
  featured: boolean
  imageUrl: string
  bannerUrl: string
  ownerName: string
  cancellationPolicyHours: number
}

export type PublicInstructorSummary = {
  id: string
  fullName: string
  bio: string
  avatarUrl: string
  specialties: string
}

export type PublicSessionSummary = {
  id: string
  studioName: string
  studioSlug: string
  classTitle: string
  discipline: string
  startsAt: string
  endsAt: string
  capacity: number
  bookedCount: number
  locationName: string
  priceEur: string
  imageUrl: string
}

export type PublicReviewSummary = {
  id: string
  reviewerName: string
  rating: number
  comment: string
  createdAt: string
}

export type LandingResponse = {
  featuredStudios: PublicStudioSummary[]
  upcomingSessions: PublicSessionSummary[]
  latestReviews: PublicReviewSummary[]
}

export type StudioDetailResponse = {
  studio: PublicStudioSummary
  instructors: PublicInstructorSummary[]
  upcomingSessions: PublicSessionSummary[]
  reviews: PublicReviewSummary[]
}

export type ProfileResponse = {
  id: string
  fullName: string
  email: string
  city: string | null
  neighborhood: string | null
  roleName: 'USER' | 'ADMIN' | 'GUEST'
  marketingOptIn: boolean
  savedPaymentMethodBrand: string | null
  savedPaymentMethodLast4: string | null
  hasSubscription: boolean
}

export type BookingCard = {
  id: string
  sessionId: string
  classTitle: string
  studioName: string
  discipline: string
  startsAt: string
  endsAt: string
  status: string
  locationName: string
  priceEur: string
  reviewLeft: boolean
}

export type DashboardResponse = {
  profile: ProfileResponse
  nextClass: BookingCard | null
  creditPack: { creditsBalance: number; creditsResetAt: string | null; status: string }
  classesAttended: number
  amountSpentEur: string
  upcomingBookings: BookingCard[]
  pastBookings: BookingCard[]
  recommendedStudios: PublicStudioSummary[]
}

export type BillingOverview = {
  profile: ProfileResponse
  subscription: { creditsBalance: number; creditsResetAt: string | null; status: string }
  receipts: Array<{
    id: string
    type: string
    status: string
    amountEur: string
    currency: string
    createdAt: string
    description: string
  }>
  customerPortalUrl: string
}

export type AdminDashboardResponse = {
  metrics: {
    totalRevenueEur: string
    activeBookings: number
    pendingApprovals: number
    systemHealth: number
    commissionRevenueEur: string
    totalUsers: number
    totalStudios: number
  }
  approvalQueue: Array<{
    id: string
    name: string
    category: string
    location: string
    submittedAt: string
    status: string
    featured: boolean
  }>
  alerts: Array<{ title: string; message: string; severity: string; timestamp: string }>
}

export type KeycloakUserSummary = {
  id: string
  email: string
  fullName: string
  roles: string[]
  enabled: boolean
}

export type PaginatedUsersResponse = {
  items: KeycloakUserSummary[]
  page: number
  size: number
  total: number
}

export type PaymentRow = {
  id: string
  type: string
  status: string
  amountEur: string
  customer: string
  createdAt: string
  reference: string
}

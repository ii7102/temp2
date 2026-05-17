import type { ReactNode } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { SiteShell } from './components/layout'
import { RequireAdmin, RequireAuth } from './auth/guards'
import { LandingPage } from './features/landing/LandingPage'
import { DiscoveryPage } from './features/discovery/DiscoveryPage'
import { StudioPage } from './features/studio/StudioPage'
import { CheckoutPage } from './features/checkout/CheckoutPage'
import { DashboardPage } from './features/dashboard/DashboardPage'
import { BillingPage } from './features/billing/BillingPage'
import { AccountPage } from './features/account/AccountPage'
import { AdminDashboardPage } from './features/admin/AdminDashboardPage'
import { NotFoundPage } from './features/errors/NotFoundPage'
import { ForbiddenPage } from './features/errors/ForbiddenPage'
import { UnauthorizedPage } from './features/errors/UnauthorizedPage'

function Shell({ children }: { children: ReactNode }) {
  return <SiteShell>{children}</SiteShell>
}

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Shell><LandingPage /></Shell>} />
      <Route path="/discover" element={<Shell><DiscoveryPage /></Shell>} />
      <Route path="/studios/:slug" element={<Shell><StudioPage /></Shell>} />
      <Route path="/checkout/:sessionId" element={<RequireAuth><Shell><CheckoutPage /></Shell></RequireAuth>} />
      <Route path="/dashboard" element={<RequireAuth><Shell><DashboardPage /></Shell></RequireAuth>} />
      <Route path="/billing" element={<RequireAuth><Shell><BillingPage /></Shell></RequireAuth>} />
      <Route path="/account" element={<RequireAuth><Shell><AccountPage /></Shell></RequireAuth>} />
      <Route path="/admin" element={<RequireAdmin><Shell><AdminDashboardPage /></Shell></RequireAdmin>} />
      <Route path="/401" element={<Shell><UnauthorizedPage /></Shell>} />
      <Route path="/403" element={<Shell><ForbiddenPage /></Shell>} />
      <Route path="/404" element={<Shell><NotFoundPage /></Shell>} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  )
}

import { Navigate, useLocation } from 'react-router-dom'
import type { ReactNode } from 'react'
import { useAuth } from './AuthProvider'

export function RequireAuth({ children }: { children: ReactNode }) {
  const auth = useAuth()
  const location = useLocation()

  if (!auth.initialized) {
    return <div className="p-8 text-text-muted">Loading PulseFit...</div>
  }

  if (!auth.authenticated) {
    return <Navigate to="/401" replace state={{ from: location.pathname }} />
  }

  return <>{children}</>
}

export function RequireAdmin({ children }: { children: ReactNode }) {
  const auth = useAuth()

  if (!auth.initialized) {
    return <div className="p-8 text-text-muted">Loading PulseFit...</div>
  }

  if (!auth.authenticated) {
    return <Navigate to="/401" replace />
  }

  if (!auth.roles.includes('admin')) {
    return <Navigate to="/403" replace />
  }

  return <>{children}</>
}

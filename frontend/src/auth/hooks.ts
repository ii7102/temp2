import { useAuth } from './AuthProvider'

export function useIsAdmin() {
  const auth = useAuth()
  return auth.roles.includes('admin')
}

export function useIsUser() {
  const auth = useAuth()
  return auth.authenticated
}

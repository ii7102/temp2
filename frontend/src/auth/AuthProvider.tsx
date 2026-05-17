import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import type Keycloak from 'keycloak-js'
import { createKeycloak } from './keycloak'
import { setAccessToken } from './tokenStore'

type AuthContextValue = {
  keycloak: Keycloak | null
  initialized: boolean
  authenticated: boolean
  roles: string[]
  name: string
  email: string
  login: () => void
  register: () => void
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [keycloak, setKeycloak] = useState<Keycloak | null>(null)
  const [initialized, setInitialized] = useState(false)
  const [authenticated, setAuthenticated] = useState(false)

  useEffect(() => {
    const client = createKeycloak()
    client
      .init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
      })
      .then((isAuthenticated) => {
        setKeycloak(client)
        setAuthenticated(isAuthenticated)
        setInitialized(true)
        setAccessToken(client.token ?? '')
      })

    const timer = window.setInterval(() => {
      if (client.authenticated) {
        client.updateToken(30).then(() => setAccessToken(client.token ?? ''))
      }
    }, 20000)

    client.onAuthSuccess = () => {
      setAuthenticated(true)
      setAccessToken(client.token ?? '')
    }
    client.onAuthLogout = () => {
      setAuthenticated(false)
      setAccessToken('')
    }
    client.onAuthRefreshSuccess = () => setAccessToken(client.token ?? '')

    return () => window.clearInterval(timer)
  }, [])

  const value = useMemo<AuthContextValue>(() => {
    const tokenData = keycloak?.tokenParsed as Record<string, unknown> | undefined
    const realmAccess = (tokenData?.realm_access as { roles?: string[] } | undefined) ?? undefined
    return {
      keycloak,
      initialized,
      authenticated,
      roles: realmAccess?.roles ?? [],
      name: (tokenData?.name as string | undefined) ?? (tokenData?.preferred_username as string | undefined) ?? 'Guest',
      email: (tokenData?.email as string | undefined) ?? '',
      login: () => keycloak?.login(),
      register: () => keycloak?.register(),
      logout: () => keycloak?.logout({ redirectUri: window.location.origin }),
    }
  }, [authenticated, initialized, keycloak])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

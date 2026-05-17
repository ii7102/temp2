import Keycloak from 'keycloak-js'

export function createKeycloak() {
  const configuredUrl = import.meta.env.VITE_KEYCLOAK_URL?.trim() || ''
  const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  const keycloakUrl = configuredUrl && (isLocalhost || !configuredUrl.includes('localhost')) ? configuredUrl : '/auth'
  const realm = import.meta.env.VITE_KEYCLOAK_REALM?.trim() || 'pulsefit'
  const clientId = import.meta.env.VITE_KEYCLOAK_CLIENT_ID?.trim() || 'pulsefit-frontend'
  return new Keycloak({
    url: keycloakUrl,
    realm,
    clientId,
  })
}

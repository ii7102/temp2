import Keycloak from 'keycloak-js'

export function createKeycloak() {
  const keycloakUrl = import.meta.env.VITE_KEYCLOAK_URL?.trim() || 'http://localhost/auth'
  const realm = import.meta.env.VITE_KEYCLOAK_REALM?.trim() || 'pulsefit'
  const clientId = import.meta.env.VITE_KEYCLOAK_CLIENT_ID?.trim() || 'pulsefit-frontend'
  return new Keycloak({
    url: keycloakUrl,
    realm,
    clientId,
  })
}

Keycloak realm import notes
==========================

This repository includes `realm-export.json` used for importing the `pulsefit` realm into Keycloak for development and initial bootstrapping.

Important production notes
- Replace placeholder URIs: `infra/keycloak/realm-export.json` now contains `__FRONTEND_HOST__` and `__BACKEND_HOST__` placeholders. Replace these with your exact production hostnames (including scheme `https://`) before importing.
- Do not commit secrets: `secret` fields and demo user passwords must not be committed. Use `PULSEFIT_BACKEND_CLIENT_SECRET` (in environment or secrets manager) or the Keycloak Admin API to set secrets at deploy-time.
- TLS is required: Serve Keycloak and your app over HTTPS and use `https://` redirect URIs.
- Import strategy: Use the realm JSON import only for initial bootstrap into an empty Keycloak data directory. For repeatable deployments or updates, prefer using the Keycloak Admin REST API or CLI tooling in CI to create/update clients and redirect URIs.

Quick example (CI-friendly) to template and import before starting Keycloak:

```bash
# Replace placeholders in CI using env vars
sed -e "s|__FRONTEND_HOST__|${FRONTEND_HOST}|g" \
    -e "s|__BACKEND_HOST__|${BACKEND_HOST}|g" \
    infra/keycloak/realm-export.json > /tmp/realm-import.json

# Start Keycloak with the templated import mounted at /opt/keycloak/data/import/realm-import.json
# or use the Keycloak Admin API to POST /admin/realms with the templated JSON
```

Security checklist
- Store `PULSEFIT_BACKEND_CLIENT_SECRET`, database credentials and `KEYCLOAK_ADMIN_PASSWORD` in a secrets manager or docker secrets, not in git.
- Use an immutable import for initial setup, then manage changes via Admin API with automation.

Contact
- If you want, I can add a small `scripts/import-realm.sh` that templates the JSON from env and calls Keycloak Admin API — say the word.

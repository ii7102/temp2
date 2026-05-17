# PulseFit

PulseFit is a neighborhood fitness-class booking platform for discovering, booking, and paying for local studio classes.

## Run locally

1. Copy `.env.example` to `.env` and fill in the Stripe and Keycloak secrets.
2. Start the stack with `docker compose up -d`.
3. Open `http://localhost` for the app, `http://localhost/api/actuator/health` for backend health, and `http://localhost/auth` for Keycloak.

## First run

Use the Keycloak admin console to confirm the imported `pulsefit` realm, then register a user through the frontend. Promote the first account to `admin` from the admin tools or the Keycloak console.
"# temp2" 

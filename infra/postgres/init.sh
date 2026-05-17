#!/bin/sh
set -eu

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  DO $$
  BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'keycloak') THEN
      CREATE ROLE keycloak WITH LOGIN PASSWORD '${KEYCLOAK_DB_PASSWORD}';
    END IF;
  END
  $$;

  CREATE DATABASE keycloak OWNER keycloak;
  GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
EOSQL

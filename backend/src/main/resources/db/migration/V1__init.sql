CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE app_users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  keycloak_id VARCHAR(128) UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  full_name VARCHAR(255) NOT NULL,
  role_name VARCHAR(32) NOT NULL,
  city VARCHAR(120),
  neighborhood VARCHAR(120),
  marketing_opt_in BOOLEAN NOT NULL DEFAULT FALSE,
  saved_payment_method_brand VARCHAR(64),
  saved_payment_method_last4 VARCHAR(4),
  stripe_customer_id VARCHAR(128),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE studios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) NOT NULL UNIQUE,
  neighborhood VARCHAR(120) NOT NULL,
  discipline VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  price_from_eur NUMERIC(10,2) NOT NULL,
  rating NUMERIC(3,2) NOT NULL DEFAULT 0,
  review_count INTEGER NOT NULL DEFAULT 0,
  approved BOOLEAN NOT NULL DEFAULT TRUE,
  featured BOOLEAN NOT NULL DEFAULT FALSE,
  owner_name VARCHAR(255) NOT NULL,
  cancellation_policy_hours INTEGER NOT NULL DEFAULT 12,
  image_url TEXT NOT NULL,
  banner_url TEXT NOT NULL,
  earnings_total_eur NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE instructors (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  studio_id UUID NOT NULL REFERENCES studios(id),
  full_name VARCHAR(255) NOT NULL,
  bio TEXT NOT NULL,
  avatar_url TEXT NOT NULL,
  specialties VARCHAR(255) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fitness_classes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  studio_id UUID NOT NULL REFERENCES studios(id),
  instructor_id UUID NOT NULL REFERENCES instructors(id),
  title VARCHAR(255) NOT NULL,
  discipline VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  duration_minutes INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE class_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  class_id UUID NOT NULL REFERENCES fitness_classes(id),
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ NOT NULL,
  capacity INTEGER NOT NULL,
  booked_count INTEGER NOT NULL DEFAULT 0,
  location_name VARCHAR(255) NOT NULL,
  price_eur NUMERIC(10,2) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE subscriptions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES app_users(id),
  stripe_subscription_id VARCHAR(128) UNIQUE,
  stripe_customer_id VARCHAR(128),
  price_id VARCHAR(128),
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  credits_balance INTEGER NOT NULL DEFAULT 0,
  credits_reset_at TIMESTAMPTZ,
  current_period_end TIMESTAMPTZ,
  cancelled_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE bookings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID NOT NULL REFERENCES class_sessions(id),
  user_id UUID NOT NULL REFERENCES app_users(id),
  subscription_id UUID REFERENCES subscriptions(id),
  payment_id UUID,
  status VARCHAR(32) NOT NULL,
  booked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  cancelled_at TIMESTAMPTZ,
  attended_at TIMESTAMPTZ,
  cancellation_reason TEXT,
  review_left BOOLEAN NOT NULL DEFAULT FALSE,
  credits_used INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID REFERENCES bookings(id),
  subscription_id UUID REFERENCES subscriptions(id),
  stripe_checkout_session_id VARCHAR(128) UNIQUE,
  stripe_payment_intent_id VARCHAR(128),
  stripe_invoice_id VARCHAR(128),
  amount_eur NUMERIC(10,2) NOT NULL,
  commission_eur NUMERIC(10,2) NOT NULL DEFAULT 0,
  currency VARCHAR(8) NOT NULL DEFAULT 'eur',
  status VARCHAR(32) NOT NULL,
  type VARCHAR(32) NOT NULL,
  idempotency_key VARCHAR(128),
  event_id VARCHAR(128) UNIQUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID NOT NULL UNIQUE REFERENCES bookings(id),
  user_id UUID NOT NULL REFERENCES app_users(id),
  studio_id UUID NOT NULL REFERENCES studios(id),
  rating INTEGER NOT NULL,
  comment TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE stripe_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  stripe_event_id VARCHAR(128) NOT NULL UNIQUE,
  event_type VARCHAR(128) NOT NULL,
  payload JSONB NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_class_sessions_starts_at ON class_sessions(starts_at);
CREATE INDEX idx_bookings_user_status ON bookings(user_id, status);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_reviews_studio ON reviews(studio_id);

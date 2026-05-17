INSERT INTO studios (id, name, slug, neighborhood, discipline, description, price_from_eur, rating, review_count, approved, featured, owner_name, cancellation_policy_hours, image_url, banner_url, earnings_total_eur)
VALUES
  ('10000000-0000-0000-0000-000000000001', 'Zenith Studio', 'zenith-studio', 'Shoreditch', 'Yoga', 'Sunlit yoga, mobility, and recovery classes with a premium neighborhood feel.', 18.00, 4.90, 188, TRUE, TRUE, 'Maya Lin', 12, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1200&q=80', 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1600&q=80', 12450.00),
  ('10000000-0000-0000-0000-000000000002', 'Iron Core Fitness', 'iron-core-fitness', 'Berlin Mitte', 'Strength', 'High-energy strength and conditioning for busy city schedules.', 22.00, 4.80, 161, TRUE, FALSE, 'Jules Becker', 8, 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=1600&q=80', 9360.00),
  ('10000000-0000-0000-0000-000000000003', 'Aqua Spin Lab', 'aqua-spin-lab', 'Hamburg HafenCity', 'Cycling', 'Rhythm-driven cycling with a boutique studio experience.', 20.00, 4.70, 94, FALSE, FALSE, 'Noah Richter', 24, 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1200&q=80', 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1600&q=80', 7800.00);

INSERT INTO instructors (id, studio_id, full_name, bio, avatar_url, specialties)
VALUES
  ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Maya Lin', 'Alignment-first instructor focused on flow and recovery.', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80', 'Yoga, Mobility'),
  ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'Marcus T.', 'Explosive coach who blends strength and conditioning.', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80', 'HIIT, Strength'),
  ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'Lena Weber', 'Cycling coach with a music-first approach.', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=400&q=80', 'Cycling, Endurance');

INSERT INTO fitness_classes (id, studio_id, instructor_id, title, discipline, description, duration_minutes)
VALUES
  ('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Rise Flow', 'Yoga', 'A steady morning sequence built for clarity and momentum.', 60),
  ('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'Core Blast 45', 'HIIT', 'Fast intervals with strength work and full-body conditioning.', 45),
  ('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', 'Endurance Ride', 'Cycling', 'High-tempo ride with progressive climbs and sprints.', 50);

INSERT INTO class_sessions (id, class_id, starts_at, ends_at, capacity, booked_count, location_name, price_eur, status)
VALUES
  ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day 1 hour', 18, 12, 'Zenith Studio, Shoreditch', 18.00, 'PUBLISHED'),
  ('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', NOW() + INTERVAL '2 day', NOW() + INTERVAL '2 day 45 minutes', 20, 16, 'Iron Core Fitness, Berlin Mitte', 22.00, 'PUBLISHED'),
  ('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', NOW() + INTERVAL '4 day', NOW() + INTERVAL '4 day 50 minutes', 22, 17, 'Aqua Spin Lab, HafenCity', 20.00, 'PUBLISHED'),
  ('40000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002', NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day 45 minutes', 20, 20, 'Iron Core Fitness, Berlin Mitte', 22.00, 'PUBLISHED'),
  ('40000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000001', NOW() + INTERVAL '7 day', NOW() + INTERVAL '7 day 1 hour', 18, 6, 'Zenith Studio, Shoreditch', 18.00, 'PUBLISHED');

INSERT INTO app_users (id, keycloak_id, email, full_name, role_name, city, neighborhood, marketing_opt_in, saved_payment_method_brand, saved_payment_method_last4, stripe_customer_id)
VALUES
  ('50000000-0000-0000-0000-000000000001', NULL, 'alex@pulsefit.local', 'Alex Morgan', 'USER', 'London', 'Shoreditch', FALSE, 'Visa', '4242', 'cus_seed_alex'),
  ('50000000-0000-0000-0000-000000000002', NULL, 'admin@pulsefit.local', 'PulseFit Admin', 'ADMIN', 'London', 'Shoreditch', FALSE, 'Mastercard', '4444', 'cus_seed_admin');

INSERT INTO subscriptions (id, user_id, stripe_subscription_id, stripe_customer_id, price_id, status, credits_balance, credits_reset_at, current_period_end)
VALUES
  ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'sub_seed_alex', 'cus_seed_alex', 'price_replace_me', 'ACTIVE', 18, NOW() + INTERVAL '15 day', NOW() + INTERVAL '15 day');

INSERT INTO bookings (id, session_id, user_id, subscription_id, payment_id, status, booked_at, attended_at, review_left, credits_used)
VALUES
  ('70000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', NULL, 'CONFIRMED', NOW() - INTERVAL '5 day', NOW() - INTERVAL '4 day', TRUE, 1),
  ('70000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000001', NULL, NULL, 'COMPLETED', NOW() - INTERVAL '4 day', NOW() - INTERVAL '3 day', TRUE, 0),
  ('70000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', NULL, NULL, 'CONFIRMED', NOW() - INTERVAL '2 day', NULL, FALSE, 0);

INSERT INTO payments (id, booking_id, subscription_id, stripe_checkout_session_id, stripe_payment_intent_id, amount_eur, commission_eur, currency, status, type, idempotency_key)
VALUES
  ('80000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', NULL, 'cs_seed_1', 'pi_seed_1', 18.00, 2.16, 'eur', 'SUCCEEDED', 'SESSION', 'seed-1'),
  ('80000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000002', NULL, 'cs_seed_2', 'pi_seed_2', 22.00, 2.64, 'eur', 'SUCCEEDED', 'SESSION', 'seed-2'),
  ('80000000-0000-0000-0000-000000000003', NULL, '60000000-0000-0000-0000-000000000001', 'cs_seed_sub', NULL, 79.00, 0.00, 'eur', 'SUCCEEDED', 'SUBSCRIPTION', 'seed-sub');

INSERT INTO reviews (id, booking_id, user_id, studio_id, rating, comment)
VALUES
  ('90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 5, 'Smooth booking flow and a great studio atmosphere.'),
  ('90000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 5, 'Great coaching and easy check-in.');

UPDATE studios SET review_count = 2 WHERE id = '10000000-0000-0000-0000-000000000001';
UPDATE studios SET review_count = 1 WHERE id = '10000000-0000-0000-0000-000000000002';

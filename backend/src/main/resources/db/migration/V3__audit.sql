ALTER TABLE payments ADD CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id);
ALTER TABLE payments ADD CONSTRAINT fk_payments_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id);
ALTER TABLE bookings ADD CONSTRAINT fk_bookings_payment FOREIGN KEY (payment_id) REFERENCES payments(id);

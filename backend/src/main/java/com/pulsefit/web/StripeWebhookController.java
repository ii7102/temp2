package com.pulsefit.web;

import com.pulsefit.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/stripe", "/stripe"})
public class StripeWebhookController {

  private final StripeWebhookService stripeWebhookService;

  public StripeWebhookController(StripeWebhookService stripeWebhookService) {
    this.stripeWebhookService = stripeWebhookService;
  }

  @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
  void webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) throws SignatureVerificationException {
    stripeWebhookService.handle(payload, signature);
  }
}

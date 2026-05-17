package com.pulsefit.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
    Security security,
    Cors cors,
    Keycloak keycloak,
    Stripe stripe) {

  public record Security(String audience) {}

  public record Cors(List<String> allowedOrigins) {}

  public record Keycloak(String issuerUri, String realm, String adminUsername, String adminPassword) {}

  public record Stripe(String secretKey, String webhookSecret, String monthlyPackPriceId,
      String dropInPriceId, String publicBaseUrl) {}
}

package com.pulsefit.config;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
          .requestMatchers("/api/public/**", "/public/**", "/api/stripe/webhook", "/stripe/webhook").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder(AppProperties properties) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(properties.keycloak().issuerUri()).build();
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(properties.keycloak().issuerUri());
    OAuth2TokenValidator<Jwt> withAudience = jwt -> {
      String audience = properties.security().audience();
      if (audience == null || audience.isBlank()) {
        return OAuth2TokenValidatorResult.success();
      }
      return jwt.getAudience().contains(audience)
          ? OAuth2TokenValidatorResult.success()
          : OAuth2TokenValidatorResult.failure(new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Missing required audience", null));
    };
    decoder.setJwtValidator(token -> {
      OAuth2TokenValidatorResult issuerResult = withIssuer.validate(token);
      if (issuerResult.hasErrors()) {
        return issuerResult;
      }
      return withAudience.validate(token);
    });
    return decoder;
  }

  @Bean
  Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Set<String> roles = jwt.getClaimAsMap("realm_access") == null
          ? Set.of()
          : ((Collection<String>) jwt.getClaimAsMap("realm_access").getOrDefault("roles", List.of())).stream().collect(Collectors.toSet());
      List<GrantedAuthority> authorities = roles.stream()
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
          .collect(Collectors.toList());
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
      return authorities;
    });
    return converter;
  }
}

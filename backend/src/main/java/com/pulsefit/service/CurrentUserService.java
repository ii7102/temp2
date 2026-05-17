package com.pulsefit.service;

import com.pulsefit.domain.AppUser;
import com.pulsefit.domain.DomainEnums.RoleName;
import com.pulsefit.repository.AppUserRepository;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

  private final AppUserRepository appUserRepository;

  public CurrentUserService(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  @Transactional
  public AppUser resolveCurrentUser(Jwt jwt) {
    String keycloakId = jwt.getSubject();
    String email = Optional.ofNullable(jwt.getClaimAsString("email")).orElseThrow(() -> new IllegalArgumentException("Missing email claim"));
    String fullName = Optional.ofNullable(jwt.getClaimAsString("name"))
        .orElse(Optional.ofNullable(jwt.getClaimAsString("preferred_username")).orElse(email));
    RoleName roleName = isAdmin(jwt) ? RoleName.ADMIN : RoleName.USER;

    AppUser user = appUserRepository.findByKeycloakId(keycloakId)
        .or(() -> appUserRepository.findByEmail(email))
        .orElseGet(AppUser::new);
    user.keycloakId = keycloakId;
    if (user.id == null) {
      user.email = email;
      user.fullName = fullName;
      user.marketingOptIn = false;
    }
    user.roleName = roleName;
    return appUserRepository.save(user);
  }

  @Transactional
  public AppUser save(AppUser user) {
    return appUserRepository.save(user);
  }

  public AppUser requireCurrentUser(Jwt jwt) {
    return resolveCurrentUser(jwt);
  }

  public boolean isAdmin(Jwt jwt) {
    Object realmAccess = jwt.getClaims().get("realm_access");
    if (!(realmAccess instanceof java.util.Map<?, ?> map)) {
      return false;
    }
    Object roles = map.get("roles");
    if (!(roles instanceof Iterable<?> iterable)) {
      return false;
    }
    for (Object role : iterable) {
      if ("admin".equals(String.valueOf(role).toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }
}

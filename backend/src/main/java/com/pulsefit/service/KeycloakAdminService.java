package com.pulsefit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsefit.config.AppProperties;
import com.pulsefit.web.dto.AdminDtos;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

  private final AppProperties properties;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient = HttpClient.newHttpClient();

  public KeycloakAdminService(AppProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  public List<AdminDtos.KeycloakUserSummary> searchUsers(String query, int page, int size) {
    try {
      String response = sendGet(adminUsersUri(query, page, size));
      JsonNode nodes = objectMapper.readTree(response);
      List<AdminDtos.KeycloakUserSummary> result = new ArrayList<>();
      for (JsonNode node : nodes) {
        result.add(new AdminDtos.KeycloakUserSummary(
            node.path("id").asText(),
            node.path("email").asText(),
            displayName(node),
            roles(node),
            node.path("enabled").asBoolean(true)));
      }
      return result;
    } catch (IOException | InterruptedException exception) {
      throw new IllegalStateException("Unable to query Keycloak users", exception);
    }
  }

  public void updateUserRole(String keycloakUserId, String roleName) {
    try {
      removeRole(keycloakUserId, "user");
      removeRole(keycloakUserId, "admin");
      addRole(keycloakUserId, roleName);
    } catch (IOException | InterruptedException exception) {
      throw new IllegalStateException("Unable to update Keycloak roles", exception);
    }
  }

  public void updateUserProfile(String keycloakUserId, String fullName, String email) {
    try {
      String[] parts = fullName.split(" ", 2);
      String firstName = parts[0];
      String lastName = parts.length > 1 ? parts[1] : "";
      JsonNode body = objectMapper.createObjectNode()
          .put("firstName", firstName)
          .put("lastName", lastName)
          .put("email", email)
          .put("username", email)
          .put("enabled", true);
      sendJson("PUT", adminBaseUri() + "/admin/realms/" + properties.keycloak().realm() + "/users/" + keycloakUserId, body.toString());
    } catch (IOException | InterruptedException exception) {
      throw new IllegalStateException("Unable to update Keycloak profile", exception);
    }
  }

  private String sendGet(String uri) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
        .header("Authorization", "Bearer " + accessToken())
        .GET()
        .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
  }

  private void sendJson(String method, String uri, String body) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
        .header("Authorization", "Bearer " + accessToken())
        .header("Content-Type", "application/json")
        .method(method, HttpRequest.BodyPublishers.ofString(body))
        .build();
    httpClient.send(request, HttpResponse.BodyHandlers.discarding());
  }

  private void removeRole(String userId, String roleName) throws IOException, InterruptedException {
    JsonNode role = objectMapper.readTree(sendGet(adminBaseUri() + "/admin/realms/" + properties.keycloak().realm() + "/roles/" + roleName));
    sendJson("DELETE", adminBaseUri() + "/admin/realms/" + properties.keycloak().realm()
        + "/users/" + userId + "/role-mappings/realm", "[" + objectMapper.writeValueAsString(role) + "]");
  }

  private void addRole(String userId, String roleName) throws IOException, InterruptedException {
    JsonNode role = objectMapper.readTree(sendGet(adminBaseUri() + "/admin/realms/" + properties.keycloak().realm() + "/roles/" + roleName));
    sendJson("POST", adminBaseUri() + "/admin/realms/" + properties.keycloak().realm()
        + "/users/" + userId + "/role-mappings/realm", "[" + objectMapper.writeValueAsString(role) + "]");
  }

  private String accessToken() throws IOException, InterruptedException {
    String body = "grant_type=password&client_id=admin-cli&username=" + encode(properties.keycloak().adminUsername())
        + "&password=" + encode(properties.keycloak().adminPassword());
    HttpRequest request = HttpRequest.newBuilder(URI.create(adminBaseUri() + "/realms/master/protocol/openid-connect/token"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
    JsonNode response = objectMapper.readTree(httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body());
    return response.path("access_token").asText();
  }

  private String adminUsersUri(String query, int page, int size) {
    StringBuilder uri = new StringBuilder(adminBaseUri()).append("/admin/realms/").append(properties.keycloak().realm())
        .append("/users?first=").append(page * size).append("&max=").append(size);
    if (query != null && !query.isBlank()) {
      uri.append("&search=").append(encode(query));
    }
    return uri.toString();
  }

  private String adminBaseUri() {
    String issuer = properties.keycloak().issuerUri();
    return issuer.substring(0, issuer.indexOf("/realms/"));
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static String displayName(JsonNode node) {
    String firstName = node.path("firstName").asText("");
    String lastName = node.path("lastName").asText("");
    String fullName = (firstName + " " + lastName).trim();
    return fullName.isBlank() ? node.path("username").asText() : fullName;
  }

  private static List<String> roles(JsonNode node) {
    List<String> roles = new ArrayList<>();
    JsonNode realmRoles = node.path("realmRoles");
    if (realmRoles.isArray()) {
      realmRoles.forEach(role -> roles.add(role.asText()));
    }
    return roles;
  }
}

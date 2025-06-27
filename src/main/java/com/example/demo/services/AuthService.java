package com.example.demo.services;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.LogoutDto;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.TemporaryPassword;
import com.example.demo.utils.KeycloakProperties;
import com.example.demo.utils.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service

public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final KeycloakProperties keycloakProperties;

    private String apiUrl;
    private String endpointLogin;
    private String introspectionEndpoint;
    private String logoutEndpoint;
    private String createUserEndpoint;

    private final RestTemplate restTemplate;


    public AuthService(KeycloakProperties keycloakProperties, RestTemplate restTemplate) {
        this.keycloakProperties = keycloakProperties;
        this.restTemplate = restTemplate;
        disableSSLVerification();
    }

    @PostConstruct
    public void init() {
        this.apiUrl = String.format("%s://%s:%d/realms/%s/protocol/openid-connect",
                keycloakProperties.getProtocol(),
                keycloakProperties.getHost(),
                keycloakProperties.getPort(),
                keycloakProperties.getRealm());

        this.endpointLogin = apiUrl + "/token";
        this.introspectionEndpoint = apiUrl + "/token/introspect";
        this.logoutEndpoint = apiUrl + "/logout";
        this.createUserEndpoint = String.format("%s://%s:%d/admin/realms/%s/users",
                keycloakProperties.getProtocol(),
                keycloakProperties.getHost(),
                keycloakProperties.getPort(),
                keycloakProperties.getRealm());

        logger.info("-----------auth.config-------------");
        logger.info("keycloak host: " + keycloakProperties.getHost());
        logger.info("keycloak realm: " + keycloakProperties.getRealm());
        logger.info("keycloak client id: " + keycloakProperties.getClientId());
        logger.info("keycloak client secret: " + keycloakProperties.getClientSecret());
        logger.info("-----------end auth.config-------------");

        logger.info("apiUrl: " + apiUrl);
        logger.info("endpointLogin: " + endpointLogin);
        logger.info("introspectionEndpoint: " + introspectionEndpoint);
        logger.info("logoutEndpoint: " + logoutEndpoint);
        logger.info("createUserEndpoint: " + createUserEndpoint);
    }

    private void disableSSLVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Failed to disable SSL verification", e);
        }
    }

    public ResponseEntity<Map> login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        logger.info("🚀 endpoint used: " + endpointLogin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("client_id", keycloakProperties.getClientId());
        map.add("client_secret", keycloakProperties.getClientSecret());
        map.add("grant_type", "password");
        map.add("scope", "offline_access");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(endpointLogin, request, Map.class);
    }

    public boolean isUserLogged(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("token", accessToken);
            map.add("client_id", keycloakProperties.getClientId());
            map.add("client_secret", keycloakProperties.getClientSecret());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(introspectionEndpoint, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return (Boolean) response.getBody().getOrDefault("active", false);
            }
            return false;
        } catch (Exception e) {
            logger.error("Error while checking user login status: {}", e.getMessage());
            return false;
        }
    }

    public boolean logout(LogoutDto logoutDto) {
        String refreshToken = logoutDto.getRefreshToken();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloakProperties.getClientId());
            map.add("client_secret", keycloakProperties.getClientSecret());
            map.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(logoutEndpoint, request, Void.class);
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> registerUser(RegisterDTO registerDTO) {
        try {
            String userId = createUser(
                    registerDTO.getPhoneNumber(),
                    TemporaryPassword.tempPassword,
                    registerDTO.getFirstName(),
                    registerDTO.getLastName()
            );
            assignRole(userId, registerDTO.getRole());
            return Map.of(
                    "status", 201,
                    "message", "registered user",
                    "userId", userId
            );
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) e;
                return Map.of(
                        "status", ex.getStatusCode().value(),
                        "message", ex.getMessage()
                );
            }
            return Map.of(
                    "status", 500,
                    "message", "server error"
            );
        }
    }

    private String createUser(String user, String pass, String firstName, String lastName) throws Exception {
        LoginDto adminLogin = new LoginDto(keycloakProperties.getAdminUser(), keycloakProperties.getAdminPass());
        ResponseEntity<Map> response = login(adminLogin);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new Exception("forbidden creating user with these credentials");
        }

        String accessToken = (String) responseBody.get("access_token");
        Map<String, Object> newUserObject = createKeycloackUserBody(user, pass, firstName, lastName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(newUserObject, headers);
        ResponseEntity<Void> createResponse = restTemplate.postForEntity(createUserEndpoint, request, Void.class);

        if (createResponse.getStatusCode() == HttpStatus.CREATED) {
            String location = createResponse.getHeaders().getFirst("Location");
            if (location != null) {
                String[] parts = location.split("/");
                return parts[parts.length - 1];
            }
        }
        throw new Exception("Failed to create user");
    }

    private Map<String, Object> createKeycloackUserBody(String user, String pass, String firstName, String lastName) {
        return Map.of(
                "username", user,
                "email", user + "@s20.ro",
                "enabled", true,
                "firstName", firstName,
                "lastName", lastName,
                "emailVerified", true,
                "credentials", List.of(
                        Map.of(
                                "type", pass,
                                "value", pass,
                                "temporary", false
                        )
                )
        );
    }

    public boolean assignRole(String userKeycloackId, Role userRole) throws Exception {
        LoginDto adminLogin = new LoginDto(keycloakProperties.getAdminUser(), keycloakProperties.getAdminPass());
        ResponseEntity<Map> loginResponse = login(adminLogin);
        Map<String, Object> loginBody = loginResponse.getBody();

        if (loginBody == null || !loginBody.containsKey("access_token")) {
            throw new Exception("forbidden creating user with these credentials");
        }

        String accessToken = (String) loginBody.get("access_token");

        // Get client ID
        String getClientUrl = String.format("%s://%s:%d/admin/realms/%s/clients?clientId=%s",
                keycloakProperties.getProtocol(), keycloakProperties.getHost(), keycloakProperties.getPort(), keycloakProperties.getRealm(), keycloakProperties.getClientId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> clientRequest = new HttpEntity<>(headers);

        ResponseEntity<Map[]> clientResponse = restTemplate.exchange(
                getClientUrl, HttpMethod.GET, clientRequest, Map[].class);

        if (clientResponse.getBody() == null || clientResponse.getBody().length == 0) {
            throw new Exception("Client not found");
        }

        String clientId = (String) clientResponse.getBody()[0].get("id");

        // Get roles
        String rolesUrl = String.format("%s://%s:%d/admin/realms/%s/clients/%s/roles",
                keycloakProperties.getProtocol(), keycloakProperties.getHost(), keycloakProperties.getPort(), keycloakProperties.getRealm(), clientId);

        ResponseEntity<Map[]> rolesResponse = restTemplate.exchange(
                rolesUrl, HttpMethod.GET, clientRequest, Map[].class);

        if (rolesResponse.getBody() == null) {
            throw new Exception("No roles found");
        }

        Map<String, Object> userRoleKeycloackObject = Arrays.stream(rolesResponse.getBody())
                .filter(role -> userRole.toString().equals(role.get("name")))
                .findFirst()
                .orElseThrow(() -> new Exception("Role not found"));

        // Assign role
        String assignRoleUrl = String.format("%s://%s:%d/admin/realms/%s/users/%s/role-mappings/clients/%s",
                keycloakProperties.getProtocol(), keycloakProperties.getHost(), keycloakProperties.getPort(), keycloakProperties.getRealm(), userKeycloackId, clientId);

        HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(
                List.of(Map.of(
                        "id", userRoleKeycloackObject.get("id"),
                        "name", userRole.toString()
                )),
                headers
        );

        ResponseEntity<Void> assignResponse = restTemplate.postForEntity(
                assignRoleUrl, roleRequest, Void.class);

        return assignResponse.getStatusCode() == HttpStatus.NO_CONTENT;
    }
}
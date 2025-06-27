package com.example.demo.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.keycloak")
@Component
@Data
public class KeycloakProperties {
    private String host;
    private int port;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String adminUser;
    private String adminPass;
    private String protocol;
}

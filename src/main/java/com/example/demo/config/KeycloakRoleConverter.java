package com.example.demo.config;

import com.example.demo.utils.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
    private final String keycloakClientId;
    private final String resourceAccessKey = "resource_access";
    private final String roles = "roles";
    private final Logger logger = Logger.getLogger(KeycloakRoleConverter.class.getName());

    public KeycloakRoleConverter(String keycloakClientId) {
        this.keycloakClientId = keycloakClientId;
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim(resourceAccessKey) == null) {
            return Set.of();
        }
        resourceAccess = jwt.getClaim(resourceAccessKey);
        if (resourceAccess.get(keycloakClientId) == null) {
            return Set.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(keycloakClientId);
        resourceRoles = (Collection<String>) resource.get(roles);

        return resourceRoles.stream().map( role -> new SimpleGrantedAuthority(Role.valueOf(role).withPrefix())).collect(Collectors.toList());
    }

    @Override
    public Collection<GrantedAuthority> convert(@org.jetbrains.annotations.NotNull Jwt jwt) {
        Collection<GrantedAuthority> defaultAuthorities = defaultConverter.convert(jwt);
        Collection<GrantedAuthority> resourceAuthorities = (Collection<GrantedAuthority>) extractResourceRoles(jwt);
        return Stream.concat(defaultAuthorities.stream(), resourceAuthorities.stream())
                .collect(Collectors.toSet());
    }
}

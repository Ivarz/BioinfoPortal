package org.biovars.bioinformaticsportal.hpc.resourceaccount;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    @Value("${keycloak.authServerUrl}")
    private String authServerUrl;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.portalRealm}")
    private String portalRealm;

    @Value("${keycloak.masterRealm}")
    private String masterRealm;

    @Value("${keycloak.adminUsername}")
    private String adminUsername;

    @Value("${keycloak.adminPassword}")
    private String adminPassword;

    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(masterRealm) // Use master realm to get admin access
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();

    }
    public List<RoleRepresentation> getClientRoles(String targetClientId) {
        Keycloak keycloak = getKeycloakClient();
        var roles = keycloak.realm(portalRealm)
                .clients()
                .get(targetClientId)
                .roles().list();
        keycloak.close();
        return roles;
    }

    public List<String> getClientRoleNames(String targetClientId) {
        return getClientRoles(targetClientId).stream().map(r -> r.getName()).collect(Collectors.toList());
    }
    public List<String> getSpaRoleNames() {
        return getClientRoleNames(clientId);
    }
}

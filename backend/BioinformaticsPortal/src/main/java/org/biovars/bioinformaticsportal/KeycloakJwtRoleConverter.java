package org.biovars.bioinformaticsportal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class KeycloakJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var extractedAuthorities = new ArrayList<GrantedAuthority>();
        Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim("resource_access");
        for (Map.Entry<String, Map<String, Collection<String>>> entry : resourceAccess.entrySet()) {
            for (String role : entry.getValue().keySet()) {
                for (String permission : entry.getValue().get(role)) {
                    extractedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+permission));
                }
            }
        }
        return extractedAuthorities;
    }
}

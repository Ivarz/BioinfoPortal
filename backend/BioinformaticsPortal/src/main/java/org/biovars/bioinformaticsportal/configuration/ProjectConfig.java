package org.biovars.bioinformaticsportal.configuration;

import org.biovars.bioinformaticsportal.ApiKeyAuthFilter;
import org.biovars.bioinformaticsportal.KeycloakJwtRoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class ProjectConfig {
    @Value("${keySetURI}")
    private String keySetUri;

    @Value("${apiKey}")
    private String apiKey;

    private final ApiKeyAuthFilter apiKeyAuthFilter;

    ProjectConfig(ApiKeyAuthFilter apiKeyAuthFilter) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.oauth2ResourceServer(
                c -> c.jwt(
                        jwt -> jwt
                            .jwkSetUri(keySetUri)
                            .jwtAuthenticationConverter(authenticationConverter())
                )
        );

        http.addFilterBefore(apiKeyAuthFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(
                c -> c
                    .requestMatchers("/api/**")
                        .permitAll()
                    .requestMatchers("/v3/api-docs/**")
                        .permitAll()
                    .requestMatchers("/swagger-ui/**")
                        .permitAll()
                    .requestMatchers("/admin/**")
                        .hasRole("portal-admin")
                    .anyRequest()
                        .authenticated()
        );

        http.csrf(
                c ->
                        c.ignoringRequestMatchers("/api/**")
        );

        http.cors(Customizer.withDefaults());

        return http.build();
    }
    Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtRoleConverter());
        return jwtAuthenticationConverter;
    }
}

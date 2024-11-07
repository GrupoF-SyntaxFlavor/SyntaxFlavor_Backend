package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${spring.keycloak.auth-server-url}")
    private String keycloakUrl;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri(
                        keycloakUrl+"/realms/syntaxflavor_users/protocol/openid-connect/certs")
                .build();
    }
}

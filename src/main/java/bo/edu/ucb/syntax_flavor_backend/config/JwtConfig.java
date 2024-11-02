package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri(
                        "http://syntaxflavor_keycloak:8080/realms/syntaxflavor_users/protocol/openid-connect/certs")
                .build(); // FIXME: Estas URL deberían ser configurables desde un archivo de propiedades o
                          // env
    }
}

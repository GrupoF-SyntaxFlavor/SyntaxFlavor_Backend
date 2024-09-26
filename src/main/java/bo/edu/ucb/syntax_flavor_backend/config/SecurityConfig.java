package bo.edu.ucb.syntax_flavor_backend.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfig {

        @Bean
        SecurityFilterChain configure(HttpSecurity http) throws Exception {
                http
                .authorizeHttpRequests(c ->
                        c
                        .requestMatchers("/api/v1/public/**").permitAll()  // Rutas públicas
                        .anyRequest().authenticated()  // Todas las demás rutas requieren autenticación
                )
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(CsrfConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
                return http.build();
        }
}
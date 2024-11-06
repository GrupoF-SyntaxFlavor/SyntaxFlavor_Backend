package bo.edu.ucb.syntax_flavor_backend.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**","/v3/api-docs**").permitAll()// Rutas públicas
                        .requestMatchers("/api/v1/public/**").permitAll() // Rutas públicas

                        // Rutas protegidas por roles
                        .requestMatchers(HttpMethod.POST, "/api/v1/bill/**").hasRole("customer")

                        .requestMatchers(HttpMethod.GET, "/api/v1/customer/profile").hasAnyRole("customer", "administrator")
                        .requestMatchers("/api/v1/customer/**").hasRole("customer") // CUSTOMER (todas las rutas bajo /api/v1/customer/**)

                        .requestMatchers("/api/v1/send-email/**").authenticated() // Requiere autenticación

                        .requestMatchers(HttpMethod.GET, "/api/v1/menu/item/**").authenticated() // Requiere autenticación (GET)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/menu/item/**").hasRole("kitchen")
                        .requestMatchers(HttpMethod.POST, "/api/v1/menu/item/**").hasRole("administrator")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/menu/item/**").hasRole("administrator")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/item/**").hasRole("administrator")

                        .requestMatchers(HttpMethod.GET, "/api/v1/order").hasAnyRole("kitchen", "administrator")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order/status").hasAnyRole("kitchen", "administrator")
                        .requestMatchers(HttpMethod.POST, "/api/v1/order/**").hasRole("customer")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/order/**").hasRole("kitchen")

                        .requestMatchers(HttpMethod.GET, "/api/v1/order/customer/**").hasAnyRole("customer", "administrator")

                        .requestMatchers(HttpMethod.GET, "/api/v1/users-with-kitchen").hasRole("administrator")

                        .anyRequest().authenticated() // Todas las demás rutas requieren
                                          // autenticación
                )
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
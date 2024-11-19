package bo.edu.ucb.syntax_flavor_backend.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .authorizeHttpRequests(c -> c
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**","/v3/api-docs**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/bill/**").hasAuthority("customer")
                    .requestMatchers(HttpMethod.GET, "/api/v1/customer/profile").hasAnyAuthority("customer", "administrator")
                .requestMatchers("/api/v1/customer/**").hasAuthority("customer")
                .requestMatchers("/api/v1/send-email/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/menu/item/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/menu/item/**").hasAuthority("kitchen")
                .requestMatchers(HttpMethod.POST, "/api/v1/menu/item/**").hasAuthority("administrator")
                .requestMatchers(HttpMethod.PUT, "/api/v1/menu/item/**").hasAuthority("administrator")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/item/**").hasAuthority("administrator")
                .requestMatchers(HttpMethod.GET, "/api/v1/order").hasAnyAuthority("kitchen", "administrator")
                .requestMatchers(HttpMethod.GET, "/api/v1/order/status").hasAnyAuthority("kitchen", "administrator")
                .requestMatchers(HttpMethod.POST, "/api/v1/order/**").hasAuthority("customer")
                .requestMatchers(HttpMethod.PUT, "/api/v1/order/**").hasAuthority("kitchen")
                .requestMatchers(HttpMethod.GET, "/api/v1/order/customer/**").hasAnyAuthority("customer", "administrator")
                .requestMatchers(HttpMethod.GET, "/api/v1/users-with-kitchen").hasAuthority("administrator")
                .requestMatchers(HttpMethod.GET, "/api/v1/report/**").hasAuthority("administrator")
                    .requestMatchers(HttpMethod.GET, "/api/v1/report/weekly-sales").hasAuthority("administrator")
                .anyRequest().authenticated()
            )
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(CsrfConfigurer::disable)
            // .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
}

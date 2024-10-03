package bo.edu.ucb.syntax_flavor_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TokenVerifier {

    @Value("${keycloak.credentials.secret}")
    private String SECRET_KEY;

    // Verify if the token is valid (checks signature)
    public boolean verifyToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))  // Use the correct secret key
                    .build()
                    .parseClaimsJws(token);  // Verify the token's signature
            return true;
        } catch (JwtException e) {
            return false;  // Signature did not match
        }
    }

    // Decode the JWT token and extract Claims (payload)
    public Claims decodeToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        }

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))  // Use the correct secret key
                .build()
                .parseClaimsJws(token)  // Parse the token
                .getBody();  // Extract Claims (payload)
    }
}
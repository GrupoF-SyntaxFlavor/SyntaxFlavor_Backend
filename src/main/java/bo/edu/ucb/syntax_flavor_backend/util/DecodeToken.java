package bo.edu.ucb.syntax_flavor_backend.util;
import com.auth0.jwt.JWT;
public class DecodeToken {

    public DecodeToken() {
    }
    // Método para extraer kcUserId desde el token JWT
    public String getKcUserIdFromToken(String token) {
        // Verificamos que el token no sea nulo o vacío
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido o ausente");
        }
        // Extraemos el kc_user_id del token
        return JWT.decode(token.substring(7)).getSubject();
    }
}


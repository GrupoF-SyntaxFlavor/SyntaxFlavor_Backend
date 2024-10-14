package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

         // Obtén la URL solicitada
        String requestURI = request.getRequestURI();

         // Define los endpoints que no requieren validación de JWT
        if (requestURI.startsWith("/api/v1/public/")) {
             // Si la URL comienza con /api/v1/public/, pasa el request sin validar el token
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No token provided");
            return;
        }

        // Aquí puedes validar el token JWT.
        // Si es inválido, puedes retornar un error como lo haces en tu API.

        filterChain.doFilter(request, response); // Continua con la cadena si el token es válido.
    }
}


package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.auth0.jwt.JWT;


import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    
    private final ObjectMapper objectMapper;

    public JwtFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
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
        SyntaxFlavorResponse<?> sfr = new SyntaxFlavorResponse<>();

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No token provided");
            return;
        }

        try {
            // Verificamos que el token no sea nulo o vacío
            if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token inválido o ausente");
            }

            // Extraemos el kc_user_id del token
            String kcUserId = JWT.decode(token.substring(7)).getSubject();
            LOGGER.info("Token valid, kcUserId: {}", kcUserId);

            // Aquí puedes almacenar el kcUserId en el contexto de seguridad o request si es necesario
            request.setAttribute("kcUserId", kcUserId);

            // Si todo es correcto, continúa con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            // Token inválido o ausente
            LOGGER.error("Invalid token: {}", e.getMessage());
            sfr.setResponseCode("SFV-401");
            sfr.setErrorMessage(e.getMessage());
            sendErrorResponse(response, sfr, HttpStatus.UNAUTHORIZED);
        }

    }
    private void sendErrorResponse(HttpServletResponse response, SyntaxFlavorResponse<?> sfr, HttpStatus status) throws IOException {
        // Configurar la respuesta como JSON
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convertir el objeto a JSON y escribirlo en la respuesta
        String jsonResponse = objectMapper.writeValueAsString(sfr);
        response.getWriter().write(jsonResponse);
    }   
}


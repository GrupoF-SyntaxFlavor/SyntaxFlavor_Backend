package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIConfiguration {

    /* @Value("${spring.keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${spring.keycloak.realm}")
    private String realm; */
    
    @Bean
    public OpenAPI defineOpenAPI(){

        // Config to use with Keycloak
        Components components = new Components()
            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
            );
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        /* Components components = new Components()
            .addSecuritySchemes("keycloak", new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                        .authorizationUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
                        .tokenUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                    )
                )   
            );
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("keycloak"); */

        // about
        Contact contact = new Contact();
        contact.setName("SyntaxFlavor");
        
        Info information = new Info()
            .title("SyntaxFlavor Backend API")
            .version("1.0")
            .description("This is the backend API for the SyntaxFlavor project, a fast food restaurant application. This backend is meant to be configured for both mobile and web frontend interfaces.")
            .contact(contact);

        return new OpenAPI()
            .info(information)
            .components(components)
            .addSecurityItem(securityRequirement); 
    }
}

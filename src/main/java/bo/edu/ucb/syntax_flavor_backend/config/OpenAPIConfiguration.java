package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfiguration {
    
    @Bean
    public OpenAPI defineOpenAPI(){

        Contact contact = new Contact();
        contact.setName("SyntaxFlavor");
        
        Info information = new Info()
            .title("SyntaxFlavor Backend API")
            .version("1.0")
            .description("This is the backend API for the SyntaxFlavor project, a fast food restaurant application. This backend is meant to be configured for both mobile and web frontend interfaces.")
            .contact(contact);

        return new OpenAPI().info(information);
    }
}

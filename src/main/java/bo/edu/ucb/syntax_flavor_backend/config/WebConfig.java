package bo.edu.ucb.syntax_flavor_backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //Esta clase es la que se encarga de la configuracion de la conexion entre el front end y el back end
    //Configura el CORS entre los puertos 8080 y 8081

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://172.18.6.210:8081","http://localhost:3000") // FIXME: Estos datos se deberían conseguir de un archivo de configuración o env
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}


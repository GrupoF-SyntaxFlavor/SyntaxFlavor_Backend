package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DotenvConfig {
    
    private final Environment environment;

    public DotenvConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Dotenv dotenv() {
        String profile = environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : "local";
        String envFile = profile.equals("docker") ? "backend_docker.env" : "backend.env";
        return Dotenv.configure()
                     .directory("src/main/resources/env")
                     .filename(envFile)
                     .load();
    }
    
}

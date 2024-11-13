package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String localIp = getLocalIp();
        String dynamicFrontendMobileUrl = "http://" + localIp + ":8081";
        String dynamicFrontendWebUrl = "http://" + localIp + ":3000";
        String dynamicFrontendWebUrl2 = "http://146.190.115.87:80";

        registry.addMapping("/**")
                .allowedOrigins(dynamicFrontendMobileUrl, dynamicFrontendWebUrl, dynamicFrontendWebUrl2, "http://146.190.115.87", "http://146.190.115.87:3000","http://localhost:8081", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost"; // Fallback in case the IP couldn't be determined
        }
    }
}

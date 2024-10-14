package bo.edu.ucb.syntax_flavor_backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean(name = "customJwtFilter")
    public FilterRegistrationBean<JwtFilter> filterConfig() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/api/v1/bill/*");
        registrationBean.addUrlPatterns("/api/v1/menu/*"); 
        registrationBean.addUrlPatterns("/api/v1/user/*");
        return registrationBean;
    }
}

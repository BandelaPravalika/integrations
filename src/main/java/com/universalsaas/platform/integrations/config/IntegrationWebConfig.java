package com.universalsaas.platform.integrations.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IntegrationWebConfig implements WebMvcConfigurer {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // RestTemplate bean moved to com.universalsaas.platform.config.RestTemplateConfig; removed to avoid duplicate bean definition.

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/integrations/**")
                .allowedOrigins(frontendUrl, "http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

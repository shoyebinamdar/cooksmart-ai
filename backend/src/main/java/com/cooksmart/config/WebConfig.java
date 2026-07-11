package com.cooksmart.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties
public class WebConfig {

    private final CookSmartProperties properties;

    public WebConfig(CookSmartProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> origins = Arrays.stream(properties.getCors().getAllowedOrigins().split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toList();
                boolean allowAll = origins.stream().anyMatch("*"::equals);
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(allowAll ? new String[] {"*"} : origins.toArray(String[]::new))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(!allowAll)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}

package com.cooksmart.config;

import java.net.URI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts Render/Heroku-style postgres:// URLs into Spring JDBC properties.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        if (databaseUrl.startsWith("jdbc:")) {
            return;
        }
        if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) {
            return;
        }

        try {
            String normalized = databaseUrl.replace("postgres://", "postgresql://");
            URI uri = URI.create(normalized);
            String userInfo = uri.getUserInfo();
            String username = null;
            String password = null;
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                if (parts.length > 1) {
                    password = parts[1];
                }
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();
            if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                jdbcUrl = jdbcUrl + "?" + uri.getQuery();
            }

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            if (username != null) {
                props.put("spring.datasource.username", username);
            }
            if (password != null) {
                props.put("spring.datasource.password", password);
            }
            environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlConversion", props));
        } catch (Exception ignored) {
            // Keep default datasource config if parsing fails
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}

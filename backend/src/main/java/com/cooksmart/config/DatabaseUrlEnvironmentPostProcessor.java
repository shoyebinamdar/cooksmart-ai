package com.cooksmart.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Converts Render/Heroku-style {@code postgres://} / {@code postgresql://} DATABASE_URL
 * values into Spring {@code spring.datasource.*} JDBC properties.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = firstNonBlank(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("SPRING_DATASOURCE_URL"));
        if (databaseUrl == null) {
            return;
        }

        // Already a JDBC URL — ensure SSL for remote hosts and publish as spring property
        if (databaseUrl.startsWith("jdbc:postgresql://") || databaseUrl.startsWith("jdbc:postgres://")) {
            String jdbcUrl = databaseUrl.replace("jdbc:postgres://", "jdbc:postgresql://");
            jdbcUrl = ensureSsl(jdbcUrl);
            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlConversion", props));
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
                username = decode(parts[0]);
                if (parts.length > 1) {
                    password = decode(parts[1]);
                }
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath() == null || uri.getPath().isBlank() ? "/cooksmart" : uri.getPath();
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;
            if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                jdbcUrl = jdbcUrl + "?" + uri.getQuery();
            }
            jdbcUrl = ensureSsl(jdbcUrl);

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            if (username != null) {
                props.put("spring.datasource.username", username);
            }
            if (password != null) {
                props.put("spring.datasource.password", password);
            }
            environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlConversion", props));
        } catch (Exception ex) {
            System.err.println("Failed to parse DATABASE_URL: " + ex.getMessage());
        }
    }

    private static String ensureSsl(String jdbcUrl) {
        if (jdbcUrl.contains("localhost") || jdbcUrl.contains("127.0.0.1")) {
            return jdbcUrl;
        }
        if (jdbcUrl.contains("sslmode=")) {
            return jdbcUrl;
        }
        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}

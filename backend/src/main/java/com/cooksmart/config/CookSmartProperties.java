package com.cooksmart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cooksmart")
public class CookSmartProperties {

    private final Cors cors = new Cors();
    private final Ai ai = new Ai();
    private final Currency currency = new Currency();

    public Cors getCors() {
        return cors;
    }

    public Ai getAi() {
        return ai;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Ai {
        private boolean enabled = true;
        private String apiKey = "";
        private String baseUrl = "https://api.openai.com/v1";
        private String model = "gpt-4o-mini";
        private int timeoutSeconds = 20;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public boolean isConfigured() {
            return enabled && apiKey != null && !apiKey.isBlank();
        }
    }

    public static class Currency {
        private String symbol = "₹";

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }
}

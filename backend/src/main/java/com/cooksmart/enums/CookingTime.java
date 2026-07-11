package com.cooksmart.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CookingTime {
    UNDER_15("<15 min", 15),
    BETWEEN_15_30("15–30 min", 30),
    BETWEEN_30_60("30–60 min", 60);

    private final String label;
    private final int maxMinutes;

    CookingTime(String label, int maxMinutes) {
        this.label = label;
        this.maxMinutes = maxMinutes;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public int getMaxMinutes() {
        return maxMinutes;
    }

    @JsonCreator
    public static CookingTime fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Cooking time must be selected");
        }
        String trimmed = value.trim();
        for (CookingTime time : values()) {
            if (time.label.equals(trimmed) || time.name().equalsIgnoreCase(trimmed.replace("-", "_").replace(" ", "_"))) {
                return time;
            }
        }
        // Accept common aliases from the UI
        return switch (trimmed.toLowerCase()) {
            case "<15", "<15 min", "under_15", "under15" -> UNDER_15;
            case "15-30", "15–30", "15-30 min", "15–30 min", "between_15_30" -> BETWEEN_15_30;
            case "30-60", "30–60", "30-60 min", "30–60 min", "between_30_60" -> BETWEEN_30_60;
            default -> throw new IllegalArgumentException("Invalid cooking time: " + value);
        };
    }
}

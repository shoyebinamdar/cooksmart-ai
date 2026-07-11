package com.cooksmart.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DietType {
    VEGETARIAN("Vegetarian"),
    NON_VEGETARIAN("Non-Vegetarian"),
    VEGAN("Vegan");

    private final String label;

    DietType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static DietType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Diet preference is required");
        }
        String normalized = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        for (DietType type : values()) {
            if (type.name().equals(normalized) || type.label.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid diet preference: " + value);
    }
}

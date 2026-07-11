package com.cooksmart.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CuisineType {
    INDIAN("Indian"),
    ITALIAN("Italian"),
    CHINESE("Chinese"),
    MEXICAN("Mexican"),
    ANY("Any");

    private final String label;

    CuisineType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static CuisineType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return ANY;
        }
        String normalized = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        for (CuisineType type : values()) {
            if (type.name().equals(normalized) || type.label.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid cuisine preference: " + value);
    }
}

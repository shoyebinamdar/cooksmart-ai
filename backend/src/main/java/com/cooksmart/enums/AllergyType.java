package com.cooksmart.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AllergyType {
    NUTS("Nuts"),
    DAIRY("Dairy"),
    GLUTEN("Gluten"),
    SEAFOOD("Seafood"),
    OTHER("Other");

    private final String label;

    AllergyType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static AllergyType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Allergy value cannot be empty");
        }
        String normalized = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        for (AllergyType type : values()) {
            if (type.name().equals(normalized) || type.label.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid allergy: " + value);
    }
}

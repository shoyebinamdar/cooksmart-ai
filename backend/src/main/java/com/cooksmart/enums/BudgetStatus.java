package com.cooksmart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BudgetStatus {
    WITHIN_BUDGET("Within Budget"),
    OVER_BUDGET("Over Budget");

    private final String label;

    BudgetStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}

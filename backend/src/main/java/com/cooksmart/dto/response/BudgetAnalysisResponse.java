package com.cooksmart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Budget feasibility analysis")
public class BudgetAnalysisResponse {

    private Double estimatedCost;
    private Double budget;
    private Double savings;
    private String status;
    private String suggestion;
    private String currencySymbol;

    public BudgetAnalysisResponse() {
    }

    public BudgetAnalysisResponse(
            Double estimatedCost,
            Double budget,
            Double savings,
            String status,
            String suggestion,
            String currencySymbol) {
        this.estimatedCost = estimatedCost;
        this.budget = budget;
        this.savings = savings;
        this.status = status;
        this.suggestion = suggestion;
        this.currencySymbol = currencySymbol;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}

package com.cooksmart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ingredient substitution suggestion")
public class SubstituteResponse {

    private String ingredient;
    private String alternative;

    public SubstituteResponse() {
    }

    public SubstituteResponse(String ingredient, String alternative) {
        this.ingredient = ingredient;
        this.alternative = alternative;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }
}

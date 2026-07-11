package com.cooksmart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SubstituteEmbeddable {

    @Column(name = "original_ingredient")
    private String ingredient;

    @Column(name = "alternative_ingredient")
    private String alternative;

    public SubstituteEmbeddable() {
    }

    public SubstituteEmbeddable(String ingredient, String alternative) {
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

package com.azonnoudo.florent.models;

public enum CategorieAnnonce {
    GENERAL("Général"),
    URGENCE("Urgence"),
    INFORMATION("Information"),
    EVENEMENT("Événement");

    private final String displayValue;

    CategorieAnnonce(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}

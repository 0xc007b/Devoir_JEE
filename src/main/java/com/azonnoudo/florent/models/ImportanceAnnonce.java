package com.azonnoudo.florent.models;

public enum ImportanceAnnonce {
    FAIBLE("Faible"),
    MOYENNE("Moyenne"),
    HAUTE("Haute");

    private final String displayValue;

    ImportanceAnnonce(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}

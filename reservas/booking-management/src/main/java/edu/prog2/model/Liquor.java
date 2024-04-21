package edu.prog2.model;

public enum Liquor {
    WHISKY("Whiskey"),
    OPORTO("Oporto"),
    VINO("Vino"),
    NINGUNO("Ninguno");

    private String value;

    private Liquor(String value) {
        this.value = value;
    }

    public static Liquor getEnum(String value) {
        if (value != null) {
            for (Liquor l : values()) {
                if (value.equalsIgnoreCase(l.value)) {
                    return l;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return value;
    }
}

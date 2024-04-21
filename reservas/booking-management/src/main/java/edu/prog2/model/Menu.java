package edu.prog2.model;

public enum Menu {
    POLLO("Pollo a la Naranja"),
    VEGETARIANO("Vegetariano"),
    PESCADO("Filete de pescado"),
    INDEFINIDO("Sin definir");

    private String value;

    private Menu(String value) {
        this.value = value;
    }

    public static Menu getEnum(String value) {
        if (value != null) {
            for (Menu m : values()) {
                if (value.equalsIgnoreCase(m.value)) {
                    return m;
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

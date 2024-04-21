package edu.prog2.model;

public enum SeatLocation {
    VENTANA("Ventana"),
    CENTRAL("Central"),
    PASILLO("Pasillo");

    private String value;

    private SeatLocation(String value) {
        this.value = value;
    }

    public static SeatLocation getEnum(String value) {
        if (value != null) {
            for (SeatLocation l : values()) {
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

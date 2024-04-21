package edu.prog2.model;

public enum FlightState {
    A_TIEMPO("A tiempo"),
    RETRASADO("Retrasado"),
    CANCELADO("Cancelado"),
    PROGRAMADO("Programado");

    private String value;

    private FlightState(String value) {
        this.value = value;
    }

    public static FlightState getEnum(String value) {
        if (value != null) {
            for (FlightState fs : values()) {
                if (value.equalsIgnoreCase(fs.value)) {
                    return fs;
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

package edu.prog2.model;

public enum UserType {
    CLIENTE("Cliente"),
    AUXILIAR("Auxiliar de taquilla"),
    ADMIN("Administrador");

    private String value;

    private UserType(String value) {
        this.value = value;
    }

    public static UserType getEnum(String value) {
        if (value != null) {
            for (UserType ut : values()) {
                if (value.equalsIgnoreCase(ut.value)) {
                    return ut;
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

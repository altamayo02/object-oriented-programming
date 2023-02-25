package edu.prog2.model;

public enum Genre {
    ACCION("Acción"),
    AVENTURA("Aventura"),
    CIENCIA_FICCION("Ciencia ficción"),
    COMEDIA("Comedia"),
    DRAMA("Drama"),
    FANTASIA("Fantasía"),
    MUSICAL("Musical"),
    NO_FICCION("No ficción / documental"),
    SUSPENSO("Suspenso"),
    TERROR("Terror"),
    DESCONOCIDO("Desconocido");

    private String value;

    private Genre(String value) {
        this.value = value;
    }

    public static Genre getEnum(String value) {
        if (value != null) {
            for (Genre g : values()) {
                if (value.equalsIgnoreCase(g.value)) {
                    return g;
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

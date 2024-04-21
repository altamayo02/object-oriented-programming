package edu.prog2.model;

import org.json.JSONObject;

// Clase Avion
public class Aircraft implements IModel {
    private String placa;
    private String modelo;

    public Aircraft(String id, String modelo) {
        this.placa = id;
        this.modelo = modelo;
    }

    public Aircraft() {
        this("", "");
    }

    public Aircraft(Aircraft a) {
        this(a.placa, a.modelo);
    }

    public Aircraft(String id) {
        this(id, "");
    }

    public Aircraft(JSONObject json) {
        this(
            json.getString("placa"),
            json.getString("modelo")
        );
    }

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String id) {
        this.placa = id;
    }

    public String getModelo() {
        return this.modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Aircraft)) {
            return false;
        }

        // Se hace casting a avion para que el compilador sepa
        // que tratamos con un objeto de clase Airplane
        Aircraft avion = (Aircraft) obj;
        // ¿Por qué avion.id, y no avion.getId()?
        return this.placa.equals(avion.placa);
    }

    @Override
    public String toString() {
        return String.format(Aircraft.formatSpec(), placa, modelo);
    }

    @Override
    public String toCSV(char septr) {
        return String.format("%s%c%s%n", placa, septr, modelo);
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    public static String formatSpec() {
        return "%-11s%-21s";
    }
}

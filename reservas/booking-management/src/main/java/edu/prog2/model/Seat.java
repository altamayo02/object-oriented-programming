package edu.prog2.model;

import java.util.InputMismatchException;

import org.json.JSONObject;

import edu.prog2.helpers.Utils;

// TODO - Get rid of getters/setters that should not exist
public class Seat implements IModel {
    protected String id;
    protected int fila;
    protected char columna;
    protected boolean disponible;
    protected Aircraft avion;
    protected SeatLocation ubicacion;

    public Seat(String id, int fila, char columna, boolean disponible, Aircraft avion) {
        this.id = id;
        this.fila = fila;
        this.columna = columna;
        this.disponible = disponible;
        this.avion = avion;

        if (columna == '0') return;
        else if ("AF".contains(columna + ""))
            ubicacion = SeatLocation.VENTANA;
        else if ("BE".contains(columna + ""))
            ubicacion = SeatLocation.CENTRAL;
        else if ("CD".contains(columna + ""))
            ubicacion = SeatLocation.PASILLO;
        else
            throw new InputMismatchException("Índice de columna inválido.");
    }

    public Seat() {
        this("S-" + Utils.getRandomKey(5), 0, '0', true, new Aircraft());
    }

    public Seat(String id) {
        this(id, 0, '0', true, new Aircraft());
    }

    public Seat(Seat s) {
        this(s.id, s.fila, s.columna, s.disponible, s.avion);
    }

    public Seat(JSONObject json) {
        this(
            json.getString("id"),
            json.getInt("fila"),
            ((char)json.getInt("columna")),
            json.getBoolean("disponible"),
            new Aircraft(json.getJSONObject("avion"))
        );
        //System.out.println(json.optString("columna").charAt(0));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(char columna) {
        this.columna = columna;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Aircraft getAvion() {
        return avion;
    }

    public void setAvion(Aircraft avion) {
        this.avion = avion;
    }

    public SeatLocation getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(SeatLocation ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPosicion() {
        return String.format("%02d%c", fila, columna);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Seat) {
            // Se hace casting a reserva para que el IDE sepa
            // que tratamos con un objeto de clase Seat
            Seat silla = (Seat) obj;
            return
                obj == this ||
                // ¿Por qué silla.id, y no silla.getId()?
                this.id.equals(silla.id) ||
                (
                    this.fila == silla.getFila() &&
                    this.columna == silla.getColumna() &&
                    this.avion.equals(silla.getAvion())
                );
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(
                formatSpec(false), getPosicion(), ubicacion, disponible ? "Sí" : "No");
    }

    @Override
    public String toCSV(char septr) {
        return String.format(
                "%s%c%d%c%c%c%s%c%s%n",
                id, septr, fila, septr, columna, septr, disponible, septr, avion.getPlaca());
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    public static String formatSpec(boolean isHeader) {
        return "%-6s%-16s%-11s";
    }
}

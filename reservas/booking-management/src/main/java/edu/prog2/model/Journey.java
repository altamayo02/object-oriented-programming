package edu.prog2.model;

import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

// Clase Trayecto
public class Journey implements IModel {
    private String id;
    private String origen;
    private String destino;
    private double costo;
    private Duration duracion;

    public Journey(String id, String origen, String destino, double costo, Duration duracion) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.costo = costo;
        this.duracion = duracion;
    }

    public Journey() {
        this("", "", "", 0, Duration.ZERO);
    }

    public Journey(Journey j) {
        this(j.id, j.origen, j.destino, j.costo, j.duracion);
    }

    public Journey(String id) {
        this(id, "", "", 0, Duration.ZERO);
    }

    public Journey(JSONObject json) {
        this(
            json.getString("id"),
            json.getString("origen"),
            json.getString("destino"),
            json.getFloat("costo"),
            Duration.parse(json.getString("duracion"))
        );
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigen() {
        return this.origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return this.destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getCosto() {
        return this.costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Duration getDuracion() {
        return this.duracion.truncatedTo(ChronoUnit.MINUTES);
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    public String strDuracion() {
        return Utils.durationString(duracion);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Journey) {
            // Se hace casting a trayecto para que el compilador sepa
            // que tratamos con un objeto de clase Journey
            Journey trayecto = (Journey) obj;
            return
                obj == this ||
                this.id.equals(trayecto.id) ||
                (
                    this.origen.equals(trayecto.origen) &&
                    this.destino.equals(trayecto.destino)
                );
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(
            Journey.formatSpec(false),
            id, origen, destino, costo, strDuracion()
        );
    }

    @Override
    public String toCSV(char septr) {
        return String.format(
            "%s%c%s%c%s%c%s%c%s%n",
            id, septr, origen, septr, destino, septr, costo, septr, duracion
        );
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    public static String formatSpec(boolean isHeader) {
        if (!isHeader) return "%-11s%-16s%-16s%-16.2f%-11s";
        return "%-11s%-16s%-16s%-16s%-11s";
    }
}

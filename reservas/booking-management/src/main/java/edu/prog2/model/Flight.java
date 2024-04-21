package edu.prog2.model;

import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import edu.prog2.helpers.Utils;

// Clase Vuelo
public class Flight implements IModel {
    private String id;
    private LocalDateTime fechaHora;
    private Journey trayecto;
    private Aircraft avion;
    private FlightState estado;

    public Flight(String id, LocalDateTime fechaHora, Journey trayecto, Aircraft avion, FlightState estado) {
        this.id = id;
        this.fechaHora = fechaHora.truncatedTo(ChronoUnit.MINUTES);
        this.trayecto = new Journey(trayecto); // COMPOSICIÓN
        this.avion = avion; // AGREGACIÓN
        // FIXME - Does this go here?
        this.estado = estado;
    }

    public Flight() {
        this("V-" + Utils.getRandomKey(5), LocalDateTime.now(), new Journey(), new Aircraft(), FlightState.PROGRAMADO);
    }

    public Flight(Flight f) {
        this(f.id, f.fechaHora, f.trayecto, f.avion, f.estado);
    }

    public Flight(String id) {
        this(id, LocalDateTime.now(), new Journey(), new Aircraft(), FlightState.PROGRAMADO);
    }

    public Flight(JSONObject json) {
        this(
            json.getString("id"),
            LocalDateTime.parse(json.getString("fechaHora")),
            new Journey(json.getJSONObject("trayecto")),
            new Aircraft(json.getJSONObject("avion")),
            FlightState.getEnum(json.getString("estado"))
        );
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return this.fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora.truncatedTo(ChronoUnit.MINUTES);
    }

    public Journey getTrayecto() {
        return this.trayecto;
    }

    public void setTrayecto(Journey trayecto) {
        this.trayecto = new Journey(trayecto);
    }

    public Aircraft getAvion() {
        return this.avion;
    }

    public void setAvion(Aircraft avion) {
        this.avion = avion;
    }

    public FlightState getEstado() {
        return this.estado;
    }

    public void setEstado(FlightState estado) {
        this.estado = estado;
    }

    public String strFechaHora() {
        return Utils.dateTimeString(fechaHora);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Flight) {
            // Se hace casting a vuelo para que el IDE sepa
            // que tratamos con un objeto de clase Flight
            Flight vuelo = (Flight) obj;
            return
              obj == this ||
              this.id.equals(vuelo.id) ||
              (
                this.fechaHora.equals(vuelo.fechaHora) &&
                this.trayecto.equals(vuelo.trayecto) &&
                this.avion.equals(vuelo.avion)
              );
        }
        return false;
    }

    // En el punto 30 del documento S5 se especifica el uso de un método
    // de la clase Vuelo para imprimir una duración, pero en el
    // modelo acordado es la clase Trayecto quien posee una duración.
    @Override
    public String toString() {
        return String.format(
            formatSpec(false), id, strFechaHora(), avion.getPlaca(), trayecto.getOrigen(),
            trayecto.getDestino(), trayecto.strDuracion(), trayecto.getCosto(), estado
        );
    }

    @Override
    public String toCSV(char septr) {
        return String.format(
            "%s%c%s%c%s%c%s%c%s%n",
            id, septr, fechaHora, septr, trayecto.getId(), septr, avion.getPlaca(), septr, estado
        );
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    public static String formatSpec(boolean isHeader) {
        if (!isHeader) return "%-11s%-17s%-11s%-16s%-16s%-11s%-11.2f%-11s";
        return "%-11s%-17s%-11s%-16s%-16s%-11s%-11s%-11s";
    }
}

package edu.prog2.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;

import edu.prog2.helpers.Utils;

public class Booking implements IModel {
    private String id;
    private LocalDateTime fechaHora;
    private double costo;
    private boolean checkIn;
    private boolean cancelada;
    private User usuario;
    private Flight vuelo;
    private Seat silla;

    public Booking(String id, User usuario, Flight vuelo, Seat silla) {
        if (!vuelo.getAvion().equals(silla.getAvion())) {
            System.out.println(vuelo.getAvion());
            System.out.println(silla.getAvion());
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.fechaHora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.costo = vuelo.getTrayecto().getCosto();
        this.checkIn = false;
        this.cancelada = false;
        this.usuario = usuario;
        this.vuelo = vuelo;
        this.silla = silla instanceof ExecutiveSeat ?
            new ExecutiveSeat((ExecutiveSeat) silla) :
            new Seat(silla);
    }

    public Booking() throws Exception {
        this("R-" + Utils.getRandomKey(5), new User(), new Flight(), new Seat());
    }

    public Booking(String id) throws Exception {
        this(id, new User(), new Flight(), new Seat());
    }

    public Booking(Booking r) throws Exception {
        this(r.id, r.usuario, r.vuelo, r.silla);
    }

    public Booking(JSONObject json) throws Exception {
        this(
            json.getString("id"),
            new User(json.getJSONObject("usuario")),
            new Flight(json.getJSONObject("vuelo")),
            new Seat()
        );
        this.silla = json.getJSONObject("silla").length() > 4 ?
            new ExecutiveSeat(json.getJSONObject("silla")) :
            new Seat(json.getJSONObject("silla"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora.truncatedTo(ChronoUnit.MINUTES);
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Flight getVuelo() {
        return vuelo;
    }

    public void setVuelo(Flight vuelo) {
        this.vuelo = vuelo;
    }

    public Seat getSilla() {
        return silla;
    }

    public void setSilla(Seat silla) {
        this.silla = silla;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Booking) {
            // Se hace casting a reserva para que el IDE sepa
            // que tratamos con un objeto de clase Booking
            Booking reserva = (Booking) obj;
            return
                obj == this ||
                this.id.equals(reserva.id) ||
                (
                    this.usuario.equals(reserva.usuario) &&
                    this.vuelo.equals(reserva.vuelo) &&
                    this.silla.equals(reserva.silla)
                );
        }
        return false;
    }

    @Override
    public String toString() {
        String str = String.format(
            "%sReserva %s - Pasajero: %s%n%s" +
            "\tFecha y hora: %s, Estado: %s, Check-in: %s%n" +
            "\tAvión: %s - %s, Silla: %s (%s)",
            Utils.PURPLE, id, usuario.getNombres() + " " + usuario.getApellidos(), Utils.CYAN,
            Utils.dateTimeString(fechaHora), vuelo.getEstado(), checkIn ? "Hecho" : "Pendiente",
            vuelo.getAvion().getModelo(), vuelo.getAvion().getPlaca(),
            silla.getPosicion(), silla instanceof ExecutiveSeat ? "Ejecutiva" : "Económica"
        );
        if (silla instanceof ExecutiveSeat) {
            // So the IDE knows it's an executive seat
            ExecutiveSeat tempSilla = (ExecutiveSeat) silla;
            str += String.format(
                ", Menú: %s, Licor: %s",
                tempSilla.getMenu(), tempSilla.getLicor()
            );
        }
        return str + "\n" + Utils.RESET;
    }

    public String toCSV(char septr) {
        String csv = String.format(
            "%s%c%s%c%s%c%s%c%s%c%b%c%b",
            id, septr, costo, septr, usuario.getId(), septr, vuelo.getId(), septr,
            silla.getId(), septr, silla.isDisponible(), septr, checkIn
        );
        if (silla instanceof ExecutiveSeat) {
            // So the IDE knows it's an executive seat
            ExecutiveSeat tempSilla = (ExecutiveSeat) silla;
            csv += String.format(
                "%c%s%c%s",
                septr, tempSilla.getMenu(), septr, tempSilla.getLicor()
            );
        }
        return csv + "\n";
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
}

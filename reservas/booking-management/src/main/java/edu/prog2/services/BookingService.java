package edu.prog2.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.ExecutiveSeat;
import edu.prog2.model.Flight;
import edu.prog2.model.FlightState;
import edu.prog2.model.Liquor;
import edu.prog2.model.Menu;
import edu.prog2.model.Booking;
import edu.prog2.model.Seat;
import edu.prog2.model.User;

public class BookingService {
    private List<Booking> reservas;
    private UserService usuarios;
    private FlightService vuelos;
    private SeatService sillas;
    private String fileName;

    public BookingService(
       UserService usuarios, FlightService vuelos, SeatService sillas
    ) throws Exception {
        this.usuarios = usuarios;
        this.vuelos = vuelos;
        this.sillas = sillas;
        reservas = new ArrayList<>();
        fileName = Utils.DB_PATH + "reservas";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV(";");
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.printf(
                "%sFalta un archivo de guardado en %s%s%s%n",
                Utils.RED, Utils.CYAN, fileName, Utils.RESET
            );
        }
    }

    public List<Booking> getList() {
        return reservas;
    }

    public UserService getUsuarios() {
        return usuarios;
    }

    public FlightService getVuelos() {
        return vuelos;
    }

    public SeatService getSillas() {
        return sillas;
    }

    public Booking get(int index) {
        return reservas.get(index);
    }

    public Booking get(Booking reserva) {
        int index = reservas.indexOf(reserva);
        // Verifica que el índice tenga sentido
        reserva = index > -1 ? reservas.get(index) : null;
        if (reserva == null) throw new NullPointerException("No se encontró la reserva.");
    
        return reserva;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new Booking(id));
    }

    public Booking get(String placa, int fila, char columna) {
        Booking reserva = null;

        for (Booking s : reservas) {
            if (true
                /*s.getAvion().getPlaca().equals(placa) &&
                s.getFila() == fila &&
                s.getColumna() == columna*/
            ) {
                reserva = s;
                break;
            }
        }
        //if (reserva == null) throw new NullPointerException("No se encontró la reserva.");
    
        return reserva;
    }

    /**
     * Modifica una reserva existente con base en un JSON con el siguiente formato:
     * {@code "idVuelo": "", "idSilla": "", "menu": "", "licor": ""}
     * @param id
     * @param json Un JSON con CUALQUIERA de las llaves mencionadas
     * @return
     * @throws Exception
     */
    public JSONObject set(String id, JSONObject json) throws Exception {
        Booking reserva = get(new Booking(id));
        if (reserva == null) {
            throw new NullPointerException("No se encontró la reserva con ID " + id);
        }
        if (reserva.isCheckIn()) {
            throw new IllegalArgumentException("Ya se realizó check-in sobre la reserva.");
        }
        if (reserva.isCancelada()) {
            throw new IllegalArgumentException("La reserva está cancelada.");
        }

        // FIXME - Which to use? get(Flight) or get(index) ?
        Flight vuelo = vuelos.get(new Flight(json.getString("idVuelo")));
        if (vuelo != null) {
            if (vuelo.getEstado() == FlightState.CANCELADO) {
                // TODO - Add IDs to exception messages?
                throw new IllegalArgumentException("El vuelo con ID " + vuelo.getId() + " está cancelado.");
            }
            if (vuelo.getFechaHora().isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                throw new IllegalArgumentException(
                    "El vuelo especificado debe tomar lugar en una hora igual o posterior a la actual."
                );
            }
            reserva.setVuelo(vuelo);
        }

        Seat silla = sillas.get(new Seat(json.getString("idSilla")));
        if (silla != null) {
            if (!silla.isDisponible()) {
                throw new IllegalArgumentException("La silla especificada no está disponible.");
            }
            reserva.getSilla().setDisponible(true);
            silla.setDisponible(false);
            reserva.setSilla(silla);
        }

        if (json.has("menu")) {
            if (reserva.getSilla() instanceof ExecutiveSeat) {
                // tempSilla.setMenu(json.getJSONObject("silla").getEnum(Menu.class, "menu"));
                ((ExecutiveSeat) silla).setMenu(Menu.getEnum(json.getString("menu")));
            } else throw new IllegalArgumentException("La silla especificada es económica, y no posee menú.");
        }

        if (json.has("licor")) {
            if (reserva.getSilla() instanceof ExecutiveSeat) {
                // tempSilla.setLicor(json.getJSONObject("silla").getEnum(Liquor.class, "licor"));
                ((ExecutiveSeat) silla).setLicor(Liquor.getEnum(json.getString("licor")));
            } else throw new IllegalArgumentException("La silla especificada es económica, y no posee licor.");
        }

        if (json.has("checkIn")) reserva.setCheckIn(json.getBoolean("checkIn"));

        if (json.has("cancelada")) reserva.setCancelada(json.getBoolean("cancelada"));
        
        Utils.writeData(reservas, fileName);
        return new JSONObject(reserva);
    }

    public JSONObject getJSON(int index) {
        return reservas.get(index).toJSONObject();
    }

    public JSONObject getJSON(Booking reserva) {
        int index = reservas.indexOf(reserva);
        return index > -1 ? getJSON(index) : null;
    }
    
    public JSONArray getJSONArray() throws Exception {
        JSONArray reservados = new JSONArray();
        for (User u : usuarios.getList()) {
            JSONObject json = new JSONObject(u).put("reservas", new JSONArray());
            for (Booking r : reservas) {
                if (u.equals(r.getUsuario())) {
                    json.getJSONArray("reservas").put(new JSONObject(r));
                }
            }
            reservados.put(json);
        }
        return reservados;
    }

    public JSONArray getJSONArray(String idUsuario) throws Exception {
        User usuario = new User(idUsuario);
        JSONArray reservasU = new JSONArray();

        for (Booking r : reservas) {
            if (usuario.equals(r.getUsuario())) {
                reservasU.put(new JSONObject(r));
            }
        }
        return reservasU;
    }

    public boolean add(Booking reserva) throws Exception {
        if (contains(reserva)) {
            throw new IOException(String.format(
               "La reserva ya existe. No se agregó.",
               reserva.getId()
            ));
        }

        boolean ok = reservas.add(reserva);
        Utils.writeData(reservas, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        Booking reserva = new Booking();
        reserva.setVuelo(vuelos.get(new Flight(json.getString("idVuelo"))));
        
        if (reserva.getVuelo().getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IOException("La fecha del vuelo debe ser igual o superior a la actual.");
        }
        
        reserva.setUsuario(usuarios.get(new User(json.getString("idUsuario"))));
        
        Seat silla = sillas.get(new Seat(json.getString("idSilla")));
        if (json.has("menu")) {
            ((ExecutiveSeat)silla).setMenu(Menu.getEnum(json.getString("menu")));
        }
        
        if (json.has("licor")) {
            ((ExecutiveSeat)silla).setLicor(Liquor.getEnum(json.getString("licor")));
        }
        silla.setDisponible(false);
        reserva.setSilla(silla);

        reserva.setCosto(reserva.getVuelo().getTrayecto().getCosto());

        add(reserva);

        return reserva.toJSONObject();
    }

    public void loadCSV(String septr) throws Exception {
        String linea;
        String id;
        double costo;
        boolean disponible;
        boolean checkIn;
        
        try (BufferedReader archivo = Files.newBufferedReader(Paths.get(fileName + ".csv"))) {
            // ¿Qué se compara? ¿El valor guardado en linea?
            while ((linea = archivo.readLine()) != null) {
                String data[] = linea.split(septr);
                id = data[0];
                costo = Double.parseDouble(data[1]);
                User usuario = usuarios.get(new User(data[2]));
                Flight vuelo = vuelos.get(new Flight(data[3]));
                Seat silla = sillas.get(new Seat(data[4]));
                disponible = Boolean.parseBoolean(data[5]);
                checkIn = Boolean.parseBoolean(data[6]);

                if (data.length == 9) { // Sillas ejecutivas
                    ExecutiveSeat tempSilla = (ExecutiveSeat) silla;
                    tempSilla.setMenu(Menu.getEnum(data[7]));
                    tempSilla.setLicor(Liquor.getEnum(data[8]));
                    silla = tempSilla;
                }

                // Asignar el costo original al costo del trayecto del vuelo
                vuelo.getTrayecto().setCosto(costo);
                
                // Reconstruir la reserva y agregarla al listado
                Booking reserva = new Booking(id, usuario, vuelo, silla);
                reserva.getSilla().setDisponible(disponible);
                reserva.setCheckIn(checkIn);
                reservas.add(reserva);
            }
        }
    }

    public List<Booking> loadJSON() throws Exception {    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            reservas.add(new Booking(jsonObj));
        }
    
        return reservas;
    }

    public boolean contains(Booking reserva) {
        return reservas.contains(reserva);
    }

    public void update() throws Exception {
        reservas = new ArrayList<>();
        loadCSV(";");
        Utils.writeJSON(reservas, fileName + ".json");
    }

    public void remove(String id) throws Exception {
        Booking reserva = new Booking(id);
        if (!reservas.remove(reserva)) {
            throw new Exception(String.format(
                "No se encontró la reserva con identificación %s", id
            ));
        }
        Utils.writeData(reservas, fileName);
    }

    public int totalFlightsBooked(User usuario) {
        int flightsBooked = 0;
        for (Booking r : reservas) {
            if (r.getUsuario().equals(usuario)) {
                flightsBooked++;
            }
        }
        return flightsBooked;
    }

    public JSONObject totalFlightsBooked(String idUsuario) throws Exception {
        User usuario = usuarios.get(new User(idUsuario));
        int flightsBooked = totalFlightsBooked(usuario);
        JSONObject json = usuario.toJSONObject()
            .put("totalReservas", flightsBooked);

        return json;
    }

    public List<Seat> reservedSeatsOnFlight(Flight vuelo) throws Exception {
        List<Seat> reservedSeats = new ArrayList<>();
        for (Booking r : reservas) {
            if (r.getVuelo() == vuelo && !r.getSilla().isDisponible()) {
                reservedSeats.add(r.getSilla());
            }
        }
        return reservedSeats;
    }

    public JSONArray reservedSeatsOnFlight(String idVuelo) throws Exception {
        Flight vuelo = vuelos.get(new Flight(idVuelo));
        return new JSONArray(reservedSeatsOnFlight(vuelo));
    }

    public boolean seatAvailableOnFlight(Seat silla, Flight vuelo) throws Exception {
        List<Seat> reservedSeats = reservedSeatsOnFlight(vuelo);

        for (Seat s : reservedSeats) {
            if (s.equals(silla)) return false;
        }
        return true;
    }

    public List<Seat> availableSeatsOnFlight(Flight vuelo) throws Exception {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat s : sillas.getList()) {
            if (s.getAvion() == vuelo.getAvion() && seatAvailableOnFlight(s, vuelo)) {
                availableSeats.add(s);
            }
        }
        return availableSeats;
    }

    public JSONArray availableSeatsOnFlight(String id) throws Exception {
        Flight vuelo = vuelos.get(new Flight(id));
        return new JSONArray(availableSeatsOnFlight(vuelo));
    }
}

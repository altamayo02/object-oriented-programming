package edu.prog2.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Aircraft;
import edu.prog2.model.Flight;
import edu.prog2.model.FlightState;
import edu.prog2.model.Journey;

public class FlightService {
    private List<Flight> vuelos;
    private JourneyService trayectos;
    private AircraftService aviones;
    private String fileName;

    public FlightService(
        JourneyService trayectos, AircraftService aviones
    ) throws Exception {
        this.trayectos = trayectos;
        this.aviones = aviones;
        vuelos = new ArrayList<>();
        fileName = Utils.DB_PATH + "vuelos";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.printf(
                "%sFalta un archivo de guardado en %s%s%s%n",
                Utils.RED, Utils.CYAN, fileName, Utils.RESET
            );
        }
    }

    public List<Flight> getList() {
        return vuelos;
    }

    public AircraftService getAviones() {
        return aviones;
    }
    
    public JourneyService getTrayectos() {
        return trayectos;
    }

    public Flight get(int index) {
        return vuelos.get(index);
    }

    public Flight get(Flight vuelo) {
        int index = vuelos.indexOf(vuelo);
        // Verifica que el índice tenga sentido
        vuelo = index > -1 ? vuelos.get(index) : null;
        if (vuelo == null) throw new NullPointerException("No se encontró el vuelo.");
    
        return vuelo;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new Flight(id));
    }

    public JSONObject set(String id, JSONObject json) throws Exception {
        Flight vuelo = get(new Flight(id));
        if (vuelo == null) {
            throw new NullPointerException("No se encontró el vuelo con ID " + id + ".");
        }

        if (json.has("fechaHora")) {
            vuelo.setFechaHora(LocalDateTime.parse(json.getString("fechaHora")));
        }

        if (json.has("idTrayecto")) {
            vuelo.setTrayecto(trayectos.get(new Journey(json.getString("idTrayecto"))));
        }

        if (json.has("idAvion")) {
            vuelo.setAvion(aviones.get(new Aircraft(json.getString("idAvion"))));
        }

        if (json.has("estado")) {
            vuelo.setEstado(FlightState.getEnum(json.getString("estado")));
        }

        Utils.writeData(vuelos, fileName);
        return new JSONObject(vuelo);
    }

    public JSONObject getJSON(int index) {
        return vuelos.get(index).toJSONObject();
    }

    public JSONObject getJSON(Flight vuelo) {
        int index = vuelos.indexOf(vuelo);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONArray getJSONArray() throws Exception {
        JSONArray jsonArr = new JSONArray(Utils.readText(fileName + ".json"));
        // Deserializar enum para el frontend
        for (int i = 0; i < jsonArr.length(); i++) {
            String estado = jsonArr.getJSONObject(i).getString("estado");
            jsonArr.getJSONObject(i).put(
                "estado", FlightState.valueOf(estado).toString()
            );
        }
        
        return jsonArr;
    }

    public boolean add(Flight vuelo) throws Exception {
        if (contains(vuelo)) {
            throw new IOException(String.format(
               "El vuelo ya existe. No se agregó.", vuelo.getId()
            ));
        }
        boolean ok = vuelos.add(vuelo);
        Utils.writeData(vuelos, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        String id = "V-" + Utils.getRandomKey(5);
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Journey trayecto = trayectos.get(new Journey(json.getString("idTrayecto")));
        Aircraft avion = aviones.get(new Aircraft(json.getString("placaAvion")));
        FlightState estado = FlightState.getEnum(json.getString("estado"));
        
        Flight vuelo = new Flight(id, fechaHora, trayecto, avion, estado);
        add(vuelo);
        return vuelo.toJSONObject();
    }

    public List<Flight> loadCSV() throws Exception {
        String text = Utils.readText(fileName + ".csv");
    
        try (Scanner sc = new Scanner(text).useDelimiter(";|,|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String id = sc.next();
                LocalDateTime fechaHora = LocalDateTime.parse(sc.next());
                Journey trayecto = new Journey(sc.next());
                Aircraft avion = new Aircraft(sc.next());
                FlightState estado = FlightState.getEnum(sc.next());
                vuelos.add(new Flight(id, fechaHora, trayectos.get(trayecto), aviones.get(avion), estado));
            }
        }

        return vuelos;
    }

    public List<Flight> loadJSON() throws Exception {    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            vuelos.add(new Flight(jsonObj));
        }
    
        return vuelos;
    }

    public boolean contains(Flight vuelo) {
        return vuelos.contains(vuelo);
    }

    public JSONArray select(String paramsVuelos) throws Exception {
        JSONObject json = Utils.paramsToJson(paramsVuelos);
        Journey trayecto = trayectos.get(
            new Journey("", json.getString("origen"), json.getString("destino"), 0.0, Duration.ZERO)
        );
    
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
    
        JSONArray array = new JSONArray();
        for (Flight v : vuelos) {
            // Una fecha no antes, o sea una después, o la misma fecha
            if (v.getTrayecto().equals(trayecto) && !v.getFechaHora().isBefore(fechaHora)) {
                array.put(new JSONObject(v));
            }
        }
    
        return array;
    }

    public void update() throws Exception {
        vuelos = new ArrayList<>();
        loadCSV();
        Utils.writeJSON(vuelos, fileName + ".json");
    }

    public void remove(String id) throws Exception {
        Flight vuelo = new Flight(id);
        
        if (Utils.jsonEntryExists(Utils.DB_PATH + "reservas", "vuelo", vuelo)) {
            throw new IllegalArgumentException("No se pudo eliminar. Hay reservas para el vuelo " + id);
        }

        if (!vuelos.remove(vuelo)) {
            throw new Exception(String.format(
                "No se encontró el vuelo con identificación %s", id
            ));
        }
        Utils.writeData(vuelos, fileName);
    }
}

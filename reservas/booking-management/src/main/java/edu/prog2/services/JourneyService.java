package edu.prog2.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Journey;

public class JourneyService {
    private List<Journey> trayectos;
    private String fileName;

    public JourneyService() throws Exception {
        trayectos = new ArrayList<>();
        fileName = Utils.DB_PATH + "trayectos";
        
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

    public List<Journey> getList() {
        return trayectos;
    }

    public Journey get(int index) {
        return trayectos.get(index);
    }

    public Journey get(Journey trayecto) {
        int index = trayectos.indexOf(trayecto);
        // Verifica que el índice tenga sentido
        trayecto = index > -1 ? trayectos.get(index) : null;
        if (trayecto == null) throw new NullPointerException("No se encontró el trayecto.");
    
        return trayecto;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new Journey(id));
    }

    public JSONObject set(String id, JSONObject json) throws Exception {
        Journey trayecto = get(new Journey(id));
        if (trayecto == null) {
            throw new NullPointerException("No se encontró el trayecto con ID " + id + ".");
        }

        trayecto.setCosto(json.getDouble("costo"));
        trayecto.setDuracion(Duration.parse(json.getString("duracion")));

        Utils.writeData(trayectos, fileName);
        return new JSONObject(trayecto);
    }

    public JSONObject getJSON(int index) {
        return trayectos.get(index).toJSONObject();
    }

    public JSONObject getJSON(Journey trayecto) {
        int index = trayectos.indexOf(trayecto);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONArray getJSONArray() throws Exception {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    public boolean add(Journey trayecto) throws Exception {
        if (contains(trayecto)) {
            throw new IOException(String.format(
               "El trayecto ya existe. No se agregó.", trayecto.getId()
            ));
        }
        boolean ok = trayectos.add(trayecto);
        Utils.writeData(trayectos, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        String id = "T-" + Utils.getRandomKey(5);
        String origen = json.getString("origen");
        String destino = json.getString("destino");
        double costo = json.getDouble("costo");
        Duration duracion = Duration.parse(json.getString("estado"));
        
        Journey trayecto = new Journey(id, origen, destino, costo, duracion);
        add(trayecto);
        return trayecto.toJSONObject();
    }

    public List<Journey> loadCSV() throws Exception {
        String text = Utils.readText(fileName + ".csv");
    
        try (Scanner sc = new Scanner(text).useDelimiter(";|,|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String id = sc.next();
                String origen = sc.next();
                String destino = sc.next();
                double costo = Double.parseDouble(sc.next());
                Duration duracion = Duration.parse(sc.next());
                trayectos.add(new Journey(id, origen, destino, costo, duracion));
            }
        }

        return trayectos;
    }

    public List<Journey> loadJSON() throws Exception {    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            trayectos.add(new Journey(jsonObj));
        }
    
        return trayectos;
    }

    public boolean contains(Journey trayecto) {
        return trayectos.contains(trayecto);
    }

    public void remove(String id) throws Exception {
        Journey trayecto = new Journey(id);

        if (Utils.jsonEntryExists(Utils.DB_PATH + "vuelos", "trayecto", trayecto)) {
            throw new IllegalArgumentException("No se pudo eliminar. Hay vuelos que utilizan el trayecto " + id);
        }

        if (!trayectos.remove(trayecto)) {
            throw new Exception(String.format(
                "No se encontró el trayecto con identificación %s", id
            ));
        }
        Utils.writeData(trayectos, fileName);
    }
}

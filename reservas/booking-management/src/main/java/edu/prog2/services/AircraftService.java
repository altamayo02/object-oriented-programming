package edu.prog2.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Aircraft;

public class AircraftService {
    private List<Aircraft> aviones;
    private String fileName;

    public AircraftService() throws Exception {
        aviones = new ArrayList<>();
        fileName = Utils.DB_PATH + "aviones";
        
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

    public List<Aircraft> getList() {
        return aviones;
    }

    public Aircraft get(int index) {
        return aviones.get(index);
    }

    public Aircraft get(Aircraft avion) {
        int index = aviones.indexOf(avion);
        // Verifica que el índice tenga sentido
        avion = index > -1 ? aviones.get(index) : null;
        if (avion == null) throw new NullPointerException("No se encontró el avion.");
    
        return avion;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new Aircraft(id));
    }

    public JSONObject set(String id, JSONObject json) throws Exception {
        Aircraft avion = get(new Aircraft(id));
        if (avion == null) {
            throw new NullPointerException("No se encontró el avión con ID " + id + ".");
        }
        
        avion.setModelo(json.getString("modelo"));
        
        Utils.writeData(aviones, fileName);
        return new JSONObject(avion);
    }

    public JSONObject getJSON(int index) {
        return aviones.get(index).toJSONObject();
    }

    public JSONObject getJSON(Aircraft avion) {
        int index = aviones.indexOf(avion);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONArray getJSONArray() throws Exception {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    public boolean add(Aircraft avion) throws Exception {
        if (contains(avion)) {
            throw new IOException("El avion ya existe. No se agregó.");
        }
        boolean ok = aviones.add(avion);
        Utils.writeData(aviones, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        String placa = "A-" + Utils.getRandomKey(5);
        String modelo = json.getString("modelo");
        
        Aircraft avion = new Aircraft(placa, modelo);
        add(avion);
        return avion.toJSONObject();
    }

    public List<Aircraft> loadCSV() throws Exception {
        String text = Utils.readText(fileName + ".csv");
    
        try (Scanner sc = new Scanner(text).useDelimiter(";|,|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String id = sc.next();
                String modelo = sc.next();
                aviones.add(new Aircraft(id, modelo));
            }
        }

        return aviones;
    }

    public List<Aircraft> loadJSON() throws Exception {    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            aviones.add(new Aircraft(jsonObj));
        }
    
        return aviones;
    }

    public boolean contains(Aircraft avion) {
        return aviones.contains(avion);
    }

    public void remove(String id) throws Exception {
        Aircraft avion = new Aircraft(id);
        
        if (Utils.jsonEntryExists(Utils.DB_PATH + "vuelos", "avion", avion)) {
            throw new IllegalArgumentException("No se pudo eliminar. Hay aviones pendientes del vuelo " + id);
        }
        if (Utils.jsonEntryExists(Utils.DB_PATH + "sillas", "avion", avion)) {
            throw new IllegalArgumentException(
                "No se pudo eliminar. Se deben eliminar las sillas del avión " + id + " primero."
            );
        }

        if (!aviones.remove(avion)) {
            throw new Exception(String.format(
                "No se encontró el avión con identificación %s", id
            ));
        }
        Utils.writeData(aviones, fileName);
    }
}

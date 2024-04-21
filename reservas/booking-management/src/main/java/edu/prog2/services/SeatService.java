package edu.prog2.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Aircraft;
import edu.prog2.model.ExecutiveSeat;
import edu.prog2.model.Liquor;
import edu.prog2.model.Menu;
import edu.prog2.model.Seat;

public class SeatService {
    private List<Seat> sillas;
    private AircraftService aviones;
    private String fileName;

    public SeatService(
        AircraftService aviones
    ) throws Exception {
        this.aviones = aviones;
        sillas = new ArrayList<>();
        fileName = Utils.DB_PATH + "sillas";

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

    public List<Seat> getList() {
        return sillas;
    }

    public AircraftService getAviones() {
        return aviones;
    }

    public Seat get(int index) {
        return sillas.get(index);
    }

    public Seat get(Seat silla) {
        int index = sillas.indexOf(silla);
        // Verifica que el índice tenga sentido
        silla = index > -1 ? sillas.get(index) : null;
        if (silla == null) throw new NullPointerException("No se encontró la silla.");
    
        return silla;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new Seat(id));
    }

    public Seat get(String placa, int fila, char columna) {
        Seat silla = null;

        for (Seat s : sillas) {
            if (s.equals(silla)) {
                silla = s;
                break;
            }
        }
        //if (silla == null) throw new NullPointerException("No se encontró la silla.");
    
        return silla;
    }

    public JSONObject set(String id, JSONObject json) throws Exception {
        Seat silla = get(new Seat(id));
        if (silla == null) {
            throw new NullPointerException("No se encontró la silla con ID " + id);
        }
        
        if (json.has("disponible")) {
            silla.setDisponible(json.getBoolean("disponible"));
        }

        if (json.has("licor")) {
            if (silla instanceof ExecutiveSeat) {
                // No confundir estas dos instrucciones:
                
                // Recupera el valor de la enumeración
                // ((ExecutiveSeat) silla).setLicor(Liquor.getEnum(json.getString("licor")));
                
                // Recupera la enumeración en sí
                // ((ExecutiveSeat) silla).setLicor(json.getEnum(Liquor.class, "licor"));
                ((ExecutiveSeat) silla).setLicor(Liquor.valueOf(json.getString("licor")));
            } else throw new IllegalArgumentException("La silla especificada es económica, y no posee licor.");
        }
    
        if (json.has("menu")) {
            if (silla instanceof ExecutiveSeat) {
                ((ExecutiveSeat) silla).setMenu(Menu.valueOf(json.getString("menu")));
            } else throw new IllegalArgumentException("La silla especificada es económica, y no posee menú.");
        }
    
        Utils.writeData(sillas, fileName);
        return new JSONObject(silla);
    }

    public JSONObject getJSON(int index) {
        return sillas.get(index).toJSONObject();
    }

    public JSONObject getJSON(Seat silla) {
        int index = sillas.indexOf(silla);
        return index > -1 ? getJSON(index) : null;
    }

    /**
     * 
     * @param json Un JSON correspondiente a una silla
     * @return
     * @throws Exception
     */
    public JSONObject getJSON(JSONObject json) throws Exception {
        json.put("id", "").put("disponible", "false")
            .put("avion", aviones.get(json.getString("avion")));
        int index = sillas.indexOf(new Seat(json));
        return index > -1 ? getJSON(index) : null;
    }

    public JSONArray getJSONArray() throws Exception {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    // FIXME - Inadequate
    public boolean add(Seat silla) throws Exception {
        if (contains(silla)) {
            throw new IOException(String.format(
               "La silla %s ya existe en el avión %s. No se agregó.",
               silla.getPosicion(), silla.getAvion().getPlaca()
            ));
            //sillas.remove(silla);
        }
        boolean ok = sillas.add(silla);
        // Utils.writeData(sillas, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        String id = "S-" + Utils.getRandomKey(5);
        int fila = json.getInt("fila");
        char columna = ((char)json.getInt("columna"));
        boolean disponible = json.getBoolean("disponible");
        Aircraft avion = aviones.get(new Aircraft(json.getString("placaAvion")));

        Seat vuelo = new Seat(id, fila, columna, disponible, avion);
        add(vuelo);
        return vuelo.toJSONObject();
    }

    // TODO - ¿Funciona?
    public void create(String placaAvion, int filasEjecutivas, int totalSillas) throws Exception {
        Aircraft avion = aviones.get(new Aircraft(placaAvion));
        // Deshacerse de sillas anteriores cuando se edita
        ArrayList<Seat> pendientes = new ArrayList<>();
        for (Seat s : sillas) {
            if (s.getAvion().equals(avion)) {
                pendientes.add(s);
            }
        }
        for (Seat s : pendientes) {
            sillas.remove(s);
        }
        Utils.writeData(sillas, fileName);

        // ¿Nos importa saber cuántas sillas hay? Posiblemente no...
        // O posiblemente sí, porque las sillas ejecutivas tienen 4 sillas solamente 💀
        int sillasEjecutivas = filasEjecutivas * 4;
        if (sillasEjecutivas > totalSillas) {
            throw new IllegalArgumentException("El número de sillas ejecutivas excede el total de sillas.");
        }
        int sillasEconomicas = totalSillas - sillasEjecutivas;
        int filasEconomicas = (int) Math.ceil(sillasEconomicas / 6.0);
        int totalFilas = filasEjecutivas + filasEconomicas;

        String columnas = "ABCDEF";
        String columnasEjec = "ACDF";
        int numColumnas;
        for (int i = 0; i < totalFilas; i++) {
            if (i < filasEjecutivas) {
                numColumnas = 4;
                if (i + 1 >= totalFilas) {
                    numColumnas = totalSillas % 4 != 0 ? totalSillas % 4 : 4;
                }
                for (int j = 0; j < numColumnas; j++) {
                    add(new ExecutiveSeat(
                        "S-" + Utils.getRandomKey(5),
                        i + 1,
                        columnasEjec.charAt(j),
                        true,
                        avion,
                        Menu.INDEFINIDO,
                        Liquor.NINGUNO
                    ));
                }
            } else {
                numColumnas = 6;
                if (i + 1 >= totalFilas) {
                    numColumnas = sillasEconomicas % 6 != 0 ? totalSillas % 6 : 6;
                }
                for (int j = 0; j < numColumnas; j++) {
                    add(new Seat(
                        "S-" + Utils.getRandomKey(5),
                        i + 1,
                        columnas.charAt(j),
                        true,
                        avion
                    ));
                }
            }       
        }

        Utils.writeData(sillas, fileName);
    }

    public void loadCSV(String septr) throws Exception {
        String linea;
        String id;
        int fila;
        char columna;
        boolean disponible;
        String placa;
        try (BufferedReader archivo = Files.newBufferedReader(Paths.get(fileName + ".csv"))) {
            // ¿Qué se compara? ¿El valor guardado en linea?
            while ((linea = archivo.readLine()) != null) {
                String data[] = linea.split(septr);
                id = data[0];
                fila = Integer.parseInt(data[1]);
                columna = data[2].charAt(0);
                disponible = Boolean.parseBoolean(data[3]);
                placa = data[4];
                Aircraft avion = aviones.get(new Aircraft(placa));
    
                if (data.length == 7) { // Sillas ejecutivas
                    Menu menu = Menu.getEnum(data[5]);
                    Liquor licor = Liquor.getEnum(data[6]);
                    sillas.add(new ExecutiveSeat(id, fila, columna, disponible, avion, menu, licor));
                } else if (data.length == 5) { // Sillas económicas
                    sillas.add(new Seat(id, fila, columna, disponible, avion));
                } else { // Error
                    throw new IOException("Se esperaban 5 o 7 datos por línea.");
                }
            }
        }
    }

    public List<Seat> loadJSON() throws Exception {
        sillas = new ArrayList<>();
    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);
    
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            if (jsonObj.length() > 6) {
                sillas.add(new ExecutiveSeat(jsonObj));
            } else {
                sillas.add(new Seat(jsonObj));
            }
        }
    
        return sillas;
    }

    public boolean contains(Seat silla) {
        return sillas.contains(silla);
    }

    public JSONArray select(String placa) throws Exception {
        if (aviones.get(placa) == null) {
            throw new IOException("El avión con placa" + placa + "no existe.");
        }

        JSONArray array = new JSONArray();
        for (Seat s : sillas) {
            if (s.getAvion().getPlaca().equals(placa)) {
                array.put(s.toJSONObject());
            }
        }
    
        return array;
    }

    public void update() throws Exception {
        sillas = new ArrayList<>();
        loadCSV(";");
        Utils.writeJSON(sillas, fileName + ".json");
    }

    public void remove(String id) throws Exception {
        Seat silla = new Seat(id);

        if (!get(silla).isDisponible()) {
            throw new IllegalArgumentException(
                "No se pudo eliminar. La silla " + silla.getId() + " está reservada."
            );
        }

        if (Utils.jsonEntryExists(Utils.DB_PATH + "reservas", "silla", silla)) {
            throw new IllegalArgumentException(
                "No se pudo eliminar. La silla " + silla.getId() + " está reservada."
            );
        }

        if (!sillas.remove(silla)) {
            throw new Exception(String.format("No se encontró la silla con identificación %s", id));
        }
        Utils.writeData(sillas, fileName);
    }

    public void removeAll(String placa) throws Exception {
        Aircraft avion = new Aircraft(placa, "");
    
        Iterator<Seat> it = sillas.iterator(); // importar de java.util
        while(it.hasNext()){
            Seat s = it.next();
            if (!s.isDisponible()) {
                throw new Exception("No se pudo eliminar las sillas. Existen reservaciones en el avión.");
            }
        }
        it = sillas.iterator();
        while(it.hasNext()){
            Seat s = it.next();
            if (s.getAvion().equals(avion)) {
                it.remove();
            }
        }

        Utils.writeData(sillas, fileName);
    }

    public JSONObject numberOfSeats(String placa) throws Exception {
        if (aviones.get(placa) == null) {
            throw new IOException("El avión con placa" + placa + "no existe.");
        }
        if (sillas == null) throw new Exception("No existen sillas.");

        int sillasEjecutivas = 0;
        int sillasEconomicas = 0;
        for (Seat s : sillas) {
            if (s.getAvion().getPlaca().equals(placa)) {
                if (s instanceof ExecutiveSeat) {
                    sillasEjecutivas++;
                    continue;
                }
                sillasEconomicas++;
            }
        }

        JSONObject json = new JSONObject()
            .put("placa", placa)
            .put("modelo", aviones.get(new Aircraft(placa)).getModelo())
            .put("totalSillas", new JSONObject()
                .put("ejecutivas", sillasEjecutivas)
                .put("economicas", sillasEconomicas));

        return json;
    }

    public JSONArray aircraftsWithNumberSeats() throws Exception {
        JSONArray array = new JSONArray();

        for (Aircraft a : aviones.getList()) {
            array.put(numberOfSeats(a.getPlaca()));
        }

        return array;
    }
}

package edu.prog2.model;

import org.json.JSONObject;
import edu.prog2.helpers.Utils;

public class ExecutiveSeat extends Seat {
    private Menu menu;
    private Liquor licor;

    public ExecutiveSeat(
        String id, int fila, char columna, boolean disponible, Aircraft avion, Menu menu, Liquor licor
    ) {
        super(id, fila, columna, disponible, avion);
        // This doesn't have to be done here, does it? It's done in the constructor above
        // ubicacion = …;
        this.menu = menu;
        this.licor = licor;
    }

    public ExecutiveSeat() {
        this("S-" + Utils.getRandomKey(5), 0, '0', true, new Aircraft(), Menu.INDEFINIDO, Liquor.NINGUNO);
    }

    public ExecutiveSeat(String id) {
        this(id, 0, '0', true, new Aircraft(), Menu.INDEFINIDO, Liquor.NINGUNO);
    }

    public ExecutiveSeat(ExecutiveSeat es) {
        this(es.id, es.fila, es.columna, es.disponible, es.avion, es.menu, es.licor);
    }

    public ExecutiveSeat(JSONObject json) {
        this(
            json.getString("id"),
            json.getInt("fila"),
            json.getString("columna").charAt(0),
            json.getBoolean("disponible"),
            new Aircraft(json.getString("avion")),
            Menu.getEnum(json.getString("menu")),
            Liquor.getEnum(json.getString("licor"))
        );
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Liquor getLicor() {
        return licor;
    }

    public void setLicor(Liquor licor) {
        this.licor = licor;
    }

    @Override
    public String toString() {
        return String.format(
                formatSpec(false), getPosicion(), ubicacion, disponible ? "Sí" : "No", menu, licor);
    }

    @Override
    public String toCSV(char septr) {
        return String.format(
            "%s%c%d%c%c%c%s%c%s%c%s%c%s%n",
            id, septr, fila, septr, columna, septr, disponible, septr,
            avion.getPlaca(), septr, menu, septr, licor
        );
    }

    public static String formatSpec(boolean isHeader) {
        return "%-6s%-16s%-11s";
    }
}

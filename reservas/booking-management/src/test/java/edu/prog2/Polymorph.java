package edu.prog2;

import java.util.ArrayList;

import edu.prog2.helpers.Utils;
import edu.prog2.model.IModel;
import edu.prog2.model.User;
import edu.prog2.services.AircraftService;
import edu.prog2.services.FlightService;
import edu.prog2.services.JourneyService;
import edu.prog2.services.UserService;

public class Polymorph {
    static UserService usuarios;
    static AircraftService aviones;
    static JourneyService trayectos;
    static FlightService vuelos;
    public static void main(String[] args) throws Exception {
        initSelf(args);

        ArrayList<IModel> list = new ArrayList<>();
        User usuario = new User("U-00001");
        list.add(usuarios.get(usuario));
        list.add(aviones.get(0));
        list.addAll(trayectos.getList());
        list.add(aviones.get(0));

        for (IModel modelObj : list) {
            System.out.println(modelObj.toCSV(';'));
        }
    }

    private static void initSelf(String[] args) throws Exception {
        // La forma documentada NO carga el idioma correctamente
        // Locale locale = new Locale.Builder().setLanguage("es").setRegion("CO").build();
        // Locale.setDefault(locale); // usar punto como separador de decimales
        // La forma obsoleta funcina correctamente
        Utils.setLocale("es", "CO", "Latn");
        System.out.println(Utils.CLEAR);

        usuarios = new UserService();
        aviones = new AircraftService();
        trayectos = new JourneyService();
        vuelos = new FlightService(trayectos, aviones);
        // sillas = new SeatService(aviones);
        // reservas = new BookingService(usuarios, vuelos, sillas);
    }
}

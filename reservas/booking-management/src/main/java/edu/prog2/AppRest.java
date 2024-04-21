package edu.prog2;

import java.nio.file.Files;
import java.nio.file.Paths;

import spark.Spark;
import static spark.Spark.*;

import edu.prog2.helpers.Utils;
import edu.prog2.services.*;
import edu.prog2.controllers.*;
import edu.prog2.helpers.StandardResponse;

public class AppRest {
    static UserService usuarios;
    static AircraftService aviones;
    static JourneyService trayectos;
    static SeatService sillas;
    static FlightService vuelos;
    static BookingService reservas;

    public static void main(String[] args) throws Exception {       
        try {
            AppRest.init(args);
            /* JSONObject j = new JSONObject().put("dato", 'A');
            System.out.println(j.toString()); */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init(String[] args) throws Exception {
        System.out.println("Envíe un argumento true o false para habilitar o deshabilitar la traza de errores en la terminal");
        boolean isDebugging = true;
        if (args.length > 0) {
            isDebugging = Boolean.parseBoolean(args[0]);
        }
        StandardResponse.DEBUGGING = isDebugging;

        // La forma documentada NO carga el idioma correctamente
        // Locale locale = new Locale.Builder().setLanguage("es").setRegion("CO").build();
        // Locale.setDefault(locale); // usar punto como separador de decimales
        // La forma obsoleta funcina correctamente
        Utils.setLocale("es", "CO", "Latn");
        System.out.println(Utils.CLEAR);

        usuarios = new UserService();
        aviones = new AircraftService();
        trayectos = new JourneyService();
        sillas = new SeatService(aviones);
        vuelos = new FlightService(trayectos, aviones);
        reservas = new BookingService(usuarios, vuelos, sillas);

        new UserController(usuarios);
        new AircraftController(aviones);
        new JourneyController(trayectos);
        new SeatController(sillas);
        new FlightController(vuelos);
        new BookingController(reservas);

        exception(Exception.class, (e, req, res) -> 
            res.body((new StandardResponse(res, e)).toString())
        );

        after("/*/:param", (req, res) -> {
            if (req.requestMethod().equals("PUT")) {
                String path = req.pathInfo().split("/")[1];

                if (path.equals("aviones")) sillas.update();
                if ("usuarios|trayectos|aviones".contains(path)) {
                    vuelos.update();
                    reservas.update();
                }
            }
        });

        get("/favicon.ico", (request, response) -> {
            response.type("image/ico");
            return Files.readAllBytes(Paths.get("./data/favicon.ico"));
        });

        enableCORS();
    }

    private static void enableCORS() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
    
        options("/*", (request, response) -> {
          String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
          }
    
          String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
          if (accessControlRequestMethod != null) {
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
          }
    
          return "ok";
        });
    
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
      }
}

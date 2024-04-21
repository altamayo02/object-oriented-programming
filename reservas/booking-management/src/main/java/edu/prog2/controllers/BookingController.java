package edu.prog2.controllers;

import spark.Request;
import spark.Response;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.helpers.Utils;
import edu.prog2.services.BookingService;

public class BookingController {
    public BookingController(final BookingService reservas) {
        path("/reservas", () -> {
            // El orden de las peticiones importa
            get("", (req, res) ->
                new StandardResponse(res, "ok", reservas.getJSONArray())
            );
            get("/filtrar/vuelo/:vuelo&disponible", (Request req, Response res) -> {
                String p = req.params(":vuelo&disponible").replace("+", " ");
                JSONObject params = Utils.paramsToJson(p);
                String idVuelo = params.getString("vuelo");
                
                JSONArray json = new JSONArray();
                if (params.getBoolean("disponible")) json = reservas.availableSeatsOnFlight(idVuelo);
                else json = reservas.reservedSeatsOnFlight(idVuelo);
                return new StandardResponse(res, "ok", json);
            });
            get("/filtrar/usuario/:id", (Request req, Response res) -> {
                String idUsuario = req.params(":id").replace("+", " ");
                JSONArray json = reservas.getJSONArray(idUsuario);
                return new StandardResponse(res, "ok", json);
            });
            get("/total/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = reservas.totalFlightsBooked(id);
                return new StandardResponse(res, "ok", json);
            });
            get("/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = reservas.get(id);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                JSONObject json = new JSONObject(req.body());
                reservas.add(json);
                return new StandardResponse(res, "ok");
            });
            put("/:id", (req, res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = reservas.set(id, json);
                return new StandardResponse(res, "ok", json);
            });
            delete("/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                reservas.remove(id);
                return new StandardResponse(response, "ok");
            });
        });
    }
}

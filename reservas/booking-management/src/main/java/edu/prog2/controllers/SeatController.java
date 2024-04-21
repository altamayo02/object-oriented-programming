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
import edu.prog2.model.Seat;
import edu.prog2.services.SeatService;

public class SeatController {
    public SeatController(final SeatService sillas) {
        path("/sillas", () -> {
            // El orden de las peticiones importa
            get("", (req, res) ->
                new StandardResponse(res, "ok", sillas.getJSONArray())
            );
            get("/filtrar/:placa", (Request req, Response res) -> {
                String placa = req.params(":placa").replace("+", " ");
                JSONArray json = sillas.select(placa);
                return new StandardResponse(res, "ok", json);
            });
            get("/buscar/:params", (req, res) -> {
                String params = req.params(":params");
                JSONObject json = Utils.paramsToJson(params);
                json = sillas.getJSON(json);
                return new StandardResponse(res, "ok", json);
            });
            get("/total", (Request req, Response res) -> {
                JSONArray json = sillas.aircraftsWithNumberSeats();
                return new StandardResponse(res, "ok", json);
            });
            get("/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = sillas.get(id);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                JSONObject json = new JSONObject(req.body());
                if (json.has("totalSillas")) {
                    sillas.create(
                        json.getString("avion"),
                        json.getInt("filasEjecutivas"),
                        json.getInt("totalSillas")
                    );
                } else {
                    json.put("id", "S-" + Utils.getRandomKey(5));
                    Seat silla = new Seat(json);
                    sillas.add(silla);
                }
                return new StandardResponse(res, "ok");
            });
            put("/:id", (req, res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = sillas.set(id, json);
                return new StandardResponse(res, "ok", json);
            });
            delete("/eliminar/avion/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                sillas.removeAll(id);
                return new StandardResponse(response, "ok");
            });
            delete("/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                sillas.remove(id);
                return new StandardResponse(response, "ok");
            });
        });
    }
}

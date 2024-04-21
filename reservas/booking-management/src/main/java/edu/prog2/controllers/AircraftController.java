package edu.prog2.controllers;

import spark.Request;
import spark.Response;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.json.JSONObject;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.model.Aircraft;
import edu.prog2.services.AircraftService;

public class AircraftController {
    public AircraftController(final AircraftService aviones) {
        path("/aviones", () -> {
            get("", (req, res) ->
                new StandardResponse(res, "ok", aviones.getJSONArray())
            );
            get("/:placa", (Request req, Response res) -> {
                String placa = req.params(":placa").replace("+", " ");
                JSONObject json = aviones.get(placa);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                JSONObject json = new JSONObject(req.body());
                aviones.add(new Aircraft(json));
                return new StandardResponse(res, "ok");
            });
            put("/:placa", (req, res) -> {
                String placa = req.params(":placa").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = aviones.set(placa, json);
                return new StandardResponse(res, "ok", json);
            });
            delete("/:placa", (request, response) -> {
                String placa = request.params(":placa").replace("+", " ");
                aviones.remove(placa);
                return new StandardResponse(response, "ok");
            });
        });
    }
}

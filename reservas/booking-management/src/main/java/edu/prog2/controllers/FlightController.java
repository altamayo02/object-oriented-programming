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
import edu.prog2.services.FlightService;

public class FlightController {
    public FlightController(final FlightService vuelos) {
        path("/vuelos", () -> {
            // El orden de las peticiones importa
            get("", (req, res) -> 
                new StandardResponse(res, "ok", vuelos.getJSONArray())
            );
            get("/filtrar/:fechaHora&origen&destino", (req, res) -> {
                String params = req.params(":fechaHora&origen&destino");
                JSONArray json = vuelos.select(params);
                return new StandardResponse(res, "ok", json);
            });
            get("/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = vuelos.get(id);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                JSONObject json = new JSONObject(req.body());
                // TODO - Find out where the rest of add(JSONObject json) went
                //vuelos.add(json);
                json = vuelos.add(json);
                return new StandardResponse(res, "ok", json);
            });
            put("/:id", (req, res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = vuelos.set(id, json);
                
                return new StandardResponse(res, "ok", json);
            });
            delete("/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                vuelos.remove(id);
                return new StandardResponse(response, "ok");
            });
        });
    }
}

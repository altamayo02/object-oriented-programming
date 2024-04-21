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
import edu.prog2.helpers.Utils;
import edu.prog2.model.Journey;
import edu.prog2.services.JourneyService;

public class JourneyController {
    public JourneyController(final JourneyService trayectos) {
        path("/trayectos", () -> {
            get("", (req, res) ->
                new StandardResponse(res, "ok", trayectos.getJSONArray())
            );
            get("/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = trayectos.get(id);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                JSONObject json = new JSONObject(req.body())
                    .put("id", "T-" + Utils.getRandomKey(5));
                Journey trayecto = new Journey(json);
                trayectos.add(trayecto);
                return new StandardResponse(res, "ok");
            });
            put("/:id", (req, res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = trayectos.set(id, json);
                return new StandardResponse(res, "ok", json);
            });
            delete("/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                trayectos.remove(id);
                return new StandardResponse(response, "ok");
            });
        });
    }
}

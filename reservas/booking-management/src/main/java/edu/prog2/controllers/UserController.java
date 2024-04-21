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
import edu.prog2.model.User;
import edu.prog2.model.UserType;
import edu.prog2.services.UserService;

public class UserController {
    public UserController(final UserService usuarios) {
        path("/usuarios", () -> {
            get("", (req, res) -> {
                // Deserializar enum para el frontend
                JSONArray jsonArr = usuarios.getJSONArray();
                for (int i = 0; i < jsonArr.length(); i++) {
                    String tipo = jsonArr.getJSONObject(i).getString("tipo");
                    jsonArr.getJSONObject(i).put(
                        "tipo", UserType.valueOf(tipo).toString()
                    );
                }

                return new StandardResponse(res, "ok", jsonArr);
            });
            get("/:id", (Request req, Response res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = usuarios.get(id);
                return new StandardResponse(res, "ok", json);
            });
            post("", (req, res) -> {
                User usuario = new User(new JSONObject(req.body()));
                usuarios.add(usuario);
                return new StandardResponse(res, "ok");
            });
            post("/autenticar", (req, res) -> {
                JSONObject json = usuarios.authenticate(new JSONObject(req.body()));
                String message = json.getString("message");
                return new StandardResponse(res, message, json.getJSONObject("usuario"));
            });
            put("/:id", (req, res) -> {
                String id = req.params(":id").replace("+", " ");
                JSONObject json = new JSONObject(req.body());
                json = usuarios.set(id, json);
                return new StandardResponse(res, "ok", json);
            });
            delete("/:id", (request, response) -> {
                String id = request.params(":id").replace("+", " ");
                usuarios.remove(id);
                return new StandardResponse(response, "ok");
            });
        });
    }
}
package edu.prog2.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Response;

public class StandardResponse {

    public static boolean DEBUGGING;
    private JSONObject json;

    public StandardResponse(Response response, int status, String message) {
        response.type("application/json");
        response.status(status);
        this.json = new JSONObject()
                .put("status", status)
                .put("message", message);
    }

    public StandardResponse(Response response, String message) {
        response.type("application/json");
        this.json = new JSONObject()
                .put("message", message);
    }

    public StandardResponse(Response response, int status, Exception e) {
        this(response, status, e.getMessage());
        if (DEBUGGING) {
            System.out.println("-".repeat(30) + " Reporte de excepciones " + "-".repeat(30));
            e.printStackTrace();
        }
    }

    public StandardResponse(Response response, Exception e) {
        this(response, e.getMessage());
        if (DEBUGGING) {
            System.out.println("-".repeat(30) + " Reporte de excepciones " + "-".repeat(30));
            e.printStackTrace();
        }
    }

    public StandardResponse(Response response, int status, String message, String data) {
        this(response, status, message);
        this.json.put("data", getData(data));
    }

    public StandardResponse(Response response, String message, String data) {
        this(response, message);
        this.json.put("data", getData(data));
    }

    public StandardResponse(Response response, int status, String message, JSONObject data) {
        this(response, status, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, String message, JSONObject data) {
        this(response, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, int status, String message, JSONArray data) {
        this(response, status, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, String message, JSONArray data) {
        this(response, message);
        this.json.put("data", data);
    }

    public JSONObject getData(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            String message = "Se esperaba una respuesta en formato JSON, pero mire esto:\n";
            return new JSONObject().put("info", message + data);
        }
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
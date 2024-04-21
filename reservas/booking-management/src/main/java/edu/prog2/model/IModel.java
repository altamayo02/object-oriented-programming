package edu.prog2.model;

import org.json.JSONObject;

public interface IModel {
    public String toCSV(char separator);
    @Override
    public boolean equals(Object obj);
    @Override
    public String toString();
    public JSONObject toJSONObject();
}
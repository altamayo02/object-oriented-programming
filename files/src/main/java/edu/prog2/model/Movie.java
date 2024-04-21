package edu.prog2.model;

import java.time.Duration;
import java.time.LocalDate;
import org.json.JSONObject;

public class Movie implements IFormatCSV {
    private String nombre;
    private Duration duracion;
    private LocalDate fechaEstreno;
    private Genre genero;
    private double recaudo;

    public Movie() {
        this("", Duration.ZERO, LocalDate.MIN, Genre.DESCONOCIDO, Double.NaN);
    }

    public Movie(String nombre, Duration duracion,
        LocalDate fechaEstreno, Genre genero, double recaudo
    ) {
        this.nombre = nombre;
        this.duracion = duracion;
        this.fechaEstreno = fechaEstreno;
        this.genero = genero;
        this.recaudo = recaudo;
    }
    
    // Necesita los accesores de cada atributo
    public Movie(JSONObject json) {
        this(
            json.getString("nombre"), 
            Duration.parse(json.getString("duracion")), 
            LocalDate.parse(json.getString("fechaEstreno")), 
            json.getEnum(Genre.class, "genero"), 
            json.getDouble("recaudo")
        );
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Duration getDuracion() {
        return this.duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    public LocalDate getFechaEstreno() {
        return this.fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public Genre getGenero() {
        return this.genero;
    }

    public void setGenero(Genre genero) {
        this.genero = genero;
    }

    public double getRecaudo() {
        return this.recaudo;
    }

    public void setRecaudo(double recaudo) {
        this.recaudo = recaudo;
    }

    public String strDuracion() {
        long hh = duracion.toHours();
        long mm = duracion.toMinutesPart();
        return String.format("%02d:%02d", hh, mm);        
    }

    @Override
    public String toString() {
        return String.format(
           "%-30s %-6s %s %-18s %13.2f", 
           nombre, strDuracion(), fechaEstreno.toString(), genero, recaudo
        );
    }

    @Override
    public String toCSV() {
        return String.format(
            "%s,%s,%s,%s,%f%n",
            nombre, duracion, fechaEstreno, genero, recaudo
        );
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
}
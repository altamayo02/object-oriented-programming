package edu.prog2.model;

import org.json.JSONObject;

import edu.prog2.helpers.Utils;

// TODO - Method that makes sure passwords are encrypted
public class User implements IModel {
    private String id;
    private String nombres;
    private String apellidos;
    private String perfil;
    private String contrasenia;
    private UserType tipo;

    public User(
        String id, String nombres, String apellidos, String perfil, String contrasenia, UserType tipo
    ) throws Exception {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.perfil = perfil;
        this.contrasenia = contrasenia;
        this.tipo = tipo;
    }

    public User() throws Exception {
        this("U-" + Utils.getRandomKey(5), "", "", "", "", UserType.CLIENTE);
    }

    public User(User u) throws Exception {
        this(u.id, u.nombres, u.apellidos, u.perfil, u.contrasenia, u.tipo);
    }

    public User(String id) throws Exception {
        this(id, "", "", "", "", UserType.CLIENTE);
    }

    public User(JSONObject json) throws Exception {
        this(
            json.getString("id"),
            json.getString("nombres"),
            json.getString("apellidos"),
            json.getString("perfil"),
            json.getString("contrasenia"),
            UserType.getEnum(json.getString("tipo"))
        );
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return this.nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) throws Exception {
        this.contrasenia = contrasenia;
    }

    public UserType getTipo() {
        return tipo;
    }

    public void setTipo(UserType tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            // Se hace casting a reserva para que el IDE sepa
            // que tratamos con un objeto de clase User
            User usuario = (User) obj;
            return
                obj == this ||
                // ¿Por qué usuario.id sirve? ¿No era privado?
                this.id.equals(usuario.id) ||
                this.perfil.equals(usuario.getPerfil());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(User.formatSpec(), id, nombres, apellidos, perfil, contrasenia, tipo);
    }

    @Override
    public String toCSV(char septr) {
        return String.format(
            "%s%c%s%c%s%c%s%c%s%c%s%n",
            id, septr, nombres, septr, apellidos, septr, perfil, septr, contrasenia, septr, tipo
        );
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject(this);
        json.remove("contrasenia");
        return json;
    }

    public static String formatSpec() {
        return "%-16s%-21s%-26s%-21s%-36s%-21s";
    }
}

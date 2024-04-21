package edu.prog2.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.User;
import edu.prog2.model.UserType;

public class UserService { //implements Iterable<T> {
    private List<User> usuarios;
    private String fileName;

    public UserService() throws Exception {
        usuarios = new ArrayList<>();
        fileName = Utils.DB_PATH + "usuarios";
        
        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.printf(
                "%sFalta un archivo de guardado en %s%s%s%n",
                Utils.RED, Utils.CYAN, fileName, Utils.RESET
            );
        }
    }

    public List<User> getList() {
        return usuarios;
    }

    public User get(int index) {
        return usuarios.get(index);
    }

    public User get(User usuario) {
        // Utiliza User.equals() internamente
        int index = usuarios.indexOf(usuario);
        // Verifica que el índice tenga sentido
        usuario = index > -1 ? usuarios.get(index) : null;
        if (usuario == null) throw new NullPointerException("No se encontró el usuario.");
    
        return usuario;
    }

    public JSONObject get(String id) throws Exception {
        return getJSON(new User(id));
    }

    public JSONObject set(String id, JSONObject json) throws Exception {
        User usuario = get(new User(id));
        if (usuario == null) {
            throw new NullPointerException("No se encontró el usuario con ID " + id + ".");
        }
        
        usuario.setNombres(json.getString("nombres"));
        usuario.setApellidos(json.getString("apellidos"));
        usuario.setPerfil(json.getString("perfil"));
        usuario.setContrasenia(Utils.md5(json.getString("contrasenia")));
        // ¿Por qué lanza excepción? Dice que la clase de "tipo" no es UserType...
        //usuario.setTipo(json.getEnum(UserType.class, "tipo"));
        usuario.setTipo(UserType.getEnum(json.getString("tipo")));

        Utils.writeData(usuarios, fileName);
        return new JSONObject(usuario);
    }

    public JSONObject getJSON(int index) {
        return usuarios.get(index).toJSONObject();
    }

    public JSONObject getJSON(User usuario) {
        int index = usuarios.indexOf(usuario);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONArray getJSONArray() throws Exception {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    public boolean add(User usuario) throws Exception {
        if (contains(usuario)) {
            throw new IOException(String.format(
               "El usuario ya existe. No se agregó.", usuario.getId()
            ));
        }
        // Según el punto 3 del taller S6, esto se implementa en la clase User
        // Pero esto dificulta la manera en que se mantienen las contraseñas encriptadas
        usuario.setContrasenia(Utils.md5(usuario.getContrasenia())); // 🧐 ¿no va?
        boolean ok = usuarios.add(usuario);
        Utils.writeData(usuarios, fileName);
        return ok;
    }

    public JSONObject add(JSONObject json) throws Exception {
        String id = "U-" + Utils.getRandomKey(5);
        String nombres = json.getString("nombres");
        String apellidos = json.getString("apellidos");
        String perfil = json.getString("perfil");
        String contrasenia = json.getString("contrasenia");
        UserType tipo = UserType.getEnum(json.getString("tipo"));
        User usuario = new User(id, nombres, apellidos, perfil, contrasenia, tipo);
        add(usuario);
        return usuario.toJSONObject();
    }

    public List<User> loadCSV() throws Exception {
        String text = Utils.readText(fileName + ".csv");
    
        try (Scanner sc = new Scanner(text).useDelimiter(";|,|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String id = sc.next();
                String nombres = sc.next();
                String apellidos = sc.next();
                String perfil = sc.next();
                String contrasenia = sc.next();
                UserType tipo = UserType.getEnum(sc.next());
                usuarios.add(
                    new User(id, nombres, apellidos, perfil, contrasenia, tipo)
                );
            }
        }

        return usuarios;
    }

    public List<User> loadJSON() throws Exception {    
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            usuarios.add(new User(jsonObj));
        }
    
        return usuarios;
    }

    public boolean contains(User usuario) {
        return usuarios.contains(usuario);
    }

    public void remove(String id) throws Exception {
        User usuario = new User(id);
        
        if (Utils.jsonEntryExists(Utils.DB_PATH + "reservas", "usuario", usuario)) {
            throw new IllegalArgumentException("No se pudo eliminar. Hay reservas a nombre del usuario " + id);
        }

        if (!usuarios.remove(usuario)) {
            throw new Exception(String.format(
                "No se encontró el usuario con identificación %s", id
            ));
        }
        Utils.writeData(usuarios, fileName);
    }

    public JSONObject authenticate(JSONObject json) throws Exception {
        User usuario = new User();
        usuario.setPerfil(json.getString("perfil"));
        try {
            usuario = get(usuario);
        } catch (Exception e) {
            throw new Exception(
                "Credenciales inválidas. Por favor verifique su nombre de usuario y contraseña."
            );
        }
        
        String contrasenia = json.getString("contrasenia");
        if (usuario.getContrasenia().equals(Utils.md5(contrasenia))) {
            return new JSONObject()
                .put("usuario", usuario.toJSONObject())
                .put("message", "ok");
        }

        throw new Exception(
            "Credenciales inválidas. Por favor verifique su nombre de usuario y contraseña."
        );
    }
}

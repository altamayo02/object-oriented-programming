package edu.prog2.helpers;

/* import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties; */
import java.util.Locale;
import java.util.Random;
import java.util.List;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
// import org.json.JSONObject;
// import org.json.Property;

import java.security.MessageDigest;
import java.math.BigInteger;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Properties;
import org.json.Property;
import org.json.JSONObject;

import edu.prog2.model.IModel;

public class Utils {
    // Colores de fuente SGR (Secuencia de control ANSI)
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String DB_PATH = "./data/";
    // Home, Clear Screen
    // https://www.unicode.org/L2/L2022/22013r-c0-c1-stability.pdf
    // ¿Es alguno más adecuado?
    // public static final String CLEAR = "\033[H\033[2J";
    public static final String CLEAR = "\u001Bc\u001B\u0094";

    private Utils() {
    }

    // ┣ VARIOUS
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static int getRandom(int min, int max) {
        if (max < min) {
            int temp = min;
            min = max;
            max = temp;
        }

        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static String getRandomKey(int stringLength) {
        int leftLimit = 48; // Numeral '0'
        int rightLimit = 90; // Uppercase letter 'Z'
        // int rightLimit = 122; // Lowercase letter 'z'
        Random random = new Random();

        // Si genera un error cambiar en POM.XML la versión 1.7 por 11
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65))// && (i <= 90 || i >= 97))
                .limit(stringLength)
                .collect(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
        return generatedString;
    }

    public static String md5(String s) throws Exception {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, m.digest()).toString(16);
    }

    public static void setLocale(String lang, String region, String script) {
        // El constructor Locale(String) está obsoleto,
        // sin embargo estará en uso hasta nuevo aviso
        // para mantener un estándar
        Locale.setDefault(new Locale(lang + "_" + region));
        /* Locale.setDefault(
            new Locale.Builder()
                .setLanguage(lang)
                .setRegion(region)
                .setScript(script)
                .build()
        ); */
    }

    // FIXME - Figure out whether this method is a good idea
    /*
     * public static void printCustom(String color, String... args) {
     *     System.out.print(color);
     * }
     */

    public static String dateTimeString(LocalDateTime dateTime) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formato);
    }

    public static String durationString(Duration duration) {
        long hh = duration.toHours();
        long mm = duration.toMinutesPart();
        return String.format("%02d:%02d", hh, mm);
    }

    /**
     * Retorna la lista de opciones pasada con el formato deseado.
     * 
     * @param items
     * @return El {@code String} que representa la lista de opciones correspondiente
     */
    public static String listString(String... items) {
        String listStr = "";
        int col1Width = 0;
        int colHeight = items.length - items.length / 2;

        for (int i = 0; i < colHeight; i++) {
            if (items[i].length() > col1Width)
                col1Width = items[i].length();
        }

        String col1 = "%-" + col1Width + "s";
        for (int i = 0; i < colHeight; i++) {
            if (items.length % 2 == 0 || colHeight != i + 1) {
                listStr += String.format(
                        "%3d - " + col1 + "    %3d - %s\n",
                        i + 1, items[i], i + 1 + colHeight, items[i + colHeight]);
            } else {
                listStr += String.format(
                        "%3d - " + col1 + "\n",
                        i + 1, items[i]);
            }
        }

        return listStr;
    }

    /**
     * Retorna la lista de opciones pasada con el formato deseado.
     * @param <T>
     * 
     * @param items
     * @return El {@code String} que representa la lista de opciones correspondiente
     */
    public static <T> String listString(List<T> items) {
        String listStr = "";

        for (int i = 0; i < items.size(); i++) {
            listStr += String.format("%3d - %s%n", i + 1, items.get(i).toString());
        }

        return listStr;
    }

    public static String formatHtml(String tag, String message) {
        String res = String.format("<%s>%s</%s>", tag, message, tag);
        return res;
    }

    public static JSONObject paramsToJson(String s) throws Exception {
        s = s.replace("&", "\n");
        StringReader reader = new StringReader(s);
        Properties properties = new Properties();
        properties.load(reader);
        return Property.toJSONObject(properties);
    }
    
    // ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    // ┣ FILE HANDLING
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static String getPath(String path) {
        Path parentPath = Paths.get(path).getParent();
        // Devuelve el directorio padre de la ruta especificada, si existe
        return parentPath == null ? null : parentPath.toString();
    }

    public static boolean fileExists(String fileName) {
        Path path = Paths.get(fileName);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public static boolean pathExists(String path) {
        Path folder = Paths.get(path);
        return Files.exists(folder) && Files.isDirectory(folder);
    }

    /**
     * Verifica en cualquier archivo de tipo JSON si un objeto está contenido en
     * uno de los objetos del array de objetos JSON que conforman el array de
     * objetos JSON contenido en el archivo.
     * @param fileName El nombre del archivo sin extensión, que contiene el array
     *  de objetos JSON.
     * @param key La clave o atributo que identifica el objeto JSON a buscar dentro
     * de cada objeto.
     * @param search El objeto JSON a buscar.
     * @return True si se encuentra que search alguno de los objetos del array.
     * @throws Exception
     */
    public static boolean jsonEntryExists(String fileName, String key, Object search) throws Exception {
        String data = readText(fileName + ".json");
        JSONArray jsonArrayData = new JSONArray(data);

        // Referencia al constructor de la clase que recibe un JSONObject como parámetro
        Constructor<?> jsonConstruct = search.getClass().getConstructor(JSONObject.class);

        for (int i = 0; i < jsonArrayData.length(); i++) {
            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

            if (jsonObj.has(key)) {
                System.out.println(jsonObj.getJSONObject(key));
                // De la instancia actual se obtiene el objeto JSON a verificar
                jsonObj = jsonObj.getJSONObject(key);
                // Se crea una instancia Java con su debida clase, a partir de jsonObj
                Object current = jsonConstruct.newInstance(jsonObj);

                if (current.equals(search)) return true;
            }
        }

        return false;
    }

    public static void createFolderIfNotExist(String folder) throws IOException {
        // Si no existe COMO CARPETA, crear la carpeta
        if (!pathExists(folder)) {
            Path dirPath = Paths.get(folder);
            Files.createDirectories(dirPath);
        }
    }

    public static Path initPath(String filePath) throws IOException {
        String path = getPath(filePath);
        createFolderIfNotExist(path);
        return Paths.get(filePath);
    }

    public static String readText(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeText(String content, String fileName) throws IOException {
        Path path = initPath(fileName);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeText(List<?> list, String fileName) throws IOException {
        Path path = initPath(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Object o : list) {
                writer.append(o.toString());
                writer.newLine();
            }
        }
    }

    public static void writeCSV(List<?> list, String fileName) throws IOException {
        Path path = initPath(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // writer.append("Titles,Go,Here\n");
            for (Object obj : list) {
                // Deja saber al ámbito que el objeto manejado implementa IFormatCSV
                IModel iObj = (IModel) (obj);
                writer.append(iObj.toCSV(';'));
            }
        }
    }

    public static void writeJSON(List<?> list, String fileName) throws IOException {
        JSONArray jsonArray = new JSONArray(list);
        writeText(jsonArray.toString(2), fileName);
    }

    public static void writeData(List<?> list, String fileName) throws IOException {
        writeCSV(list, fileName + ".csv");
        writeJSON(list, fileName + ".json");
    }
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
}
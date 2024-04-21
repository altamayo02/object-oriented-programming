package edu.prog2.helpers;

/* import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties; */
import java.util.Locale;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
// import org.json.JSONObject;
// import org.json.Property;

import edu.prog2.model.IFormatCSV;

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
    public static final String PATH = "./data/";
    // Home, Clear Screen
    // https://www.unicode.org/L2/L2022/22013r-c0-c1-stability.pdf
    // ¿Es alguno más adecuado?
    //public static final String CLEAR = "\033[H\033[2J";
    public static final String CLEAR = "\u001Bc\u001B\u0094";
    
    private Utils() {}

//┣ VARIOUS ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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
        //int rightLimit = 122; // Lowercase letter 'z'
        Random random = new Random();
        
        // Si genera un error cambiar en POM.XML la versión 1.7 por 11
        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65))// && (i <= 90 || i >= 97))
          .limit(stringLength)
          .collect(
            StringBuilder::new, 
            StringBuilder::appendCodePoint, 
            StringBuilder::append
          )
          .toString();
        return generatedString;
    }
    
    public static void setLocale(String lang) {
        Locale.setDefault(new Locale(lang));
    }

    // TODO - Figure out whether this method is a good idea
    /* public static void printCustom(String color, String... args) {
        System.out.print(color);
    } */

    public static String dateTimeString(LocalDateTime dateTime) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formato);
    }

    /**
     * Retorna la lista de opciones pasada con el formato deseado.
     * @param options
     * @return El {@code String} que representa la lista de opciones correspondiente
     */
    public static String optionsString(String... options) {
        String menuStr = "";
        int col1Width = 0;
        int colHeight = options.length - options.length/2;
        

        for (int i = 0; i < colHeight; i++) {
            if (options[i].length() > col1Width) col1Width = options[i].length();
        }

        String col1 = "%-" + col1Width + "s";
        for (int i = 0; i < colHeight; i++) {
            if (options.length % 2 == 0 || colHeight != i+1) {
                menuStr += String.format(
                    "%3d - " + col1 + "    %3d - %s\n",
                    i+1, options[i], i+1+colHeight, options[i+colHeight]
                );
            } else {
                menuStr += String.format(
                    "%3d - " + col1 + "\n",
                    i+1, options[i]
                );
            }
        }

        return menuStr;
    }
//┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

//┣ FILE HANDLING ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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
            writer.append("Nombre,Duración,Fecha Estreno,Género,Recaudo\n");
            for (Object obj : list) {
                // Deja saber al ámbito que el objeto manejado implementa IFormatCSV
                IFormatCSV iObj = (IFormatCSV)(obj);
                writer.append(iObj.toCSV());
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
//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
}
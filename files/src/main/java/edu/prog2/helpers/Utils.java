package edu.prog2.helpers;

/*
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;*/
import java.util.Locale;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;

// import org.json.JSONArray;  // falta incorporar JSON al proyecto
// import org.json.JSONObject;
// import org.json.Property;

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
    // ¿Es alguno más adecuado?
    //public static final String CLEAR = "\033[H\033[2J";
    public static final String CLEAR = "\u001Bc\u001Bc";
    
    private Utils() {}
    
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

    public static String getPath(String path) {
        Path parentPath = Paths.get(path).getParent();
        // Devuelve el directorio padre de la ruta especificada, si existe
        return parentPath == null ? null : parentPath.toString();
    }

    public static void setLocale(String lang) {
        Locale.setDefault(new Locale(lang));
    }

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
                    "%2d - " + col1 + "    %2d - %s\n",
                    i+1, options[i], i+1+colHeight, options[i+colHeight]
                );
            } else {
                menuStr += String.format(
                    "%2d - " + col1 + "\n",
                    i+1, options[i]
                );
            }
        }

        return menuStr;
    }

    public static boolean fileExists(String fileName) {
        Path dirPath = Paths.get(fileName);
        return Files.exists(dirPath) && !Files.isDirectory(dirPath);
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

    public static void writeText(List<?> list, String fileName) throws Exception {
        Path path = initPath(fileName);
        try (
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        ) {
            for (Object o : list) {
                writer.append(o.toString());
                writer.newLine();
            }
        }
    }
}
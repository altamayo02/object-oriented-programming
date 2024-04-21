package edu.prog2;

import java.util.Scanner;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDate;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;

import edu.prog2.helpers.*;
import edu.prog2.model.*;

// TODO - Standardize use of:
// New-lines
// Identifier this
// Variable names
// Multi-lines
// Method structure
public class App 
{
    public static void main( String[] args ) {
        menu();
    }

    private static void menu() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            try {
                int option = readOption();
                switch (option) {
                    case 1:
                        testFileExists();
                        break;

                    case 2:
                        testPathExists();
                        break;

                    case 3:
                        testCreateFolderIfNotExist();
                        break;

                    case 4:
                        testGetPath();
                        break;

                    case 5:
                        testInitPath();
                        break;

                    case 6:
                        testWriteText();
                        break;

                    case 7:
                        testReadText();
                        break;

                    case 8:
                        testWriteCSV();
                        break;

                    case 9:
                        csvToJSON();
                        break;

                    case 10:
                        csvToJSON2();
                        break;

                    case 11:
                        csvToJSONArray();
                        break;

                    case 12:
                        csvToJSONFile();
                        break;

                    case 99:
                        testWriteText2();
                        break;

                    case 0:
                        exit();
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } while (true);
    }

    private static void init() {
        System.out.print(Utils.CLEAR);
        Utils.setLocale("es_CO");
    }

    private static void exit() {
        System.out.print(Utils.CLEAR);
        // Limpia el caché de escritura
        System.out.flush();
        System.exit(0);
    }

    private static int readOption() {
        String options = String.format("%sMenú de opciones:%s\n", Utils.PURPLE, Utils.RESET)
          + Utils.optionsString("testFileExists", "testPathExists", "testCreateFolderIfNotExist",
            "testGetPath", "testInitPath", "testWriteText", "testReadText", "testWriteCSV",
            "csvToJSON", "csvToJSON2", "csvToJSONArray", "csvToJSONFile")
          + String.format("%3d - testWriteText2\n", 99)
          + String.format("%s%3d - Salir%s\n", Utils.RED, 0, Utils.RESET)
          + String.format("\nElija una opción (%s0 para salir%s) > ", Utils.RED, Utils.RESET);

        int option = Keyboard.readInt(options);
        return option;
    }

    private static void testFileExists() {
        String filePath = "./src/main/java/edu/prog2/model/Pelicula.java";
        boolean existe = Utils.fileExists(filePath);
        System.out.printf("¿Existe Pelicula.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        filePath = "./src/main/java/edu/prog2/model/Movie.java";
        existe = Utils.fileExists(filePath);
        System.out.printf("¿Existe Movie.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        filePath = "./src/main/java/edu/prog2/helpers/Keyboard.java";
        existe = Utils.fileExists(filePath);
        System.out.printf("¿Existe Keyboard.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        filePath = "./src/main/java/edu/prog2/helpers/";
        existe = Utils.fileExists(filePath);
        System.out.printf("¿Existe helpers? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);
    }

    private static void testPathExists() {
        String path = "./src/main/java/edu/prog2/model/Pelicula.java";
        boolean existe = Utils.pathExists(path);
        System.out.printf("¿Existe Pelicula.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        path = "./src/main/java/edu/prog2/model/Movie.java";
        existe = Utils.pathExists(path);
        System.out.printf("¿Existe Movie.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        path = "./src/main/java/edu/prog2/helpers/Keyboard.java";
        existe = Utils.pathExists(path);
        System.out.printf("¿Existe Keyboard.java? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);

        path = "./src/main/java/edu/prog2/helpers/";
        existe = Utils.pathExists(path);
        System.out.printf("¿Existe helpers? = %s%s%s%n", Utils.CYAN, existe, Utils.RESET);
    }

    private static void testCreateFolderIfNotExist() throws IOException {
        // Crea una carpeta con un nombre aleatorio de 5 caracteres
        String folder = String.format("%s%s", Utils.PATH, Utils.getRandomKey(5));
        Utils.createFolderIfNotExist(folder);
        System.out.printf(
            "%sCarpeta %s creada y lista para ser usada%s\n",
            Utils.GREEN, folder, Utils.RESET
        );
    }

    private static void testGetPath() {
        String ruta = Utils.getPath("./src/main/java/edu/prog2/model/Genre.java");
        System.out.printf("La ruta padre es: %s%s%s%n", Utils.CYAN, ruta, Utils.RESET);
        ruta = Utils.getPath("./src/main/java/edu/prog2/model/");
        System.out.printf("La ruta padre es: %s%s%s%n", Utils.CYAN, ruta, Utils.RESET);
    }

    private static void testInitPath() throws IOException {
        System.out.printf(
            "%sDirectorios creados para los archivos %s y %s%s\n",
            Utils.GREEN,
            Utils.initPath(Utils.PATH + "varios/prueba.txt"),
            Utils.initPath(Utils.PATH + "trazas/prueba.txt"),
            Utils.RESET
        );
    }

    private static void testWriteText() throws IOException {
        // futbolistas.txt
        ArrayList<String> jugadores = new ArrayList<>();
        jugadores.add("Diego Armando Maradona");
        jugadores.add("Lionel Messi");
        jugadores.add("Cristiano Ronaldo");
        jugadores.add("Johan Cruyff");
        jugadores.add("Franz Beckenbauer");

        Utils.writeText(jugadores, Utils.PATH + "futbolistas.txt");
        
        // peliculas.txt
        ArrayList<Movie> peliculas = new ArrayList<>();
        peliculas.add(
            new Movie(
                "Harry el sucio", 
                Duration.parse("PT1H50M"), 
                LocalDate.parse("1971-12-23"), 
                Genre.COMEDIA,
                932989142
            )
        );
        peliculas.add(
            new Movie(
                "El padrino", 
                Duration.parse("PT2H10M"), 
                LocalDate.parse("1972-03-24"), 
                Genre.ACCION,
                32989142
            )
        );
        peliculas.add(
            new Movie(
                "Matrix I", 
                Duration.parse("PT1H50M"), 
                LocalDate.parse("1999-05-21"), 
                Genre.CIENCIA_FICCION,
                1632989142
            )
        );
        peliculas.add(
            new Movie(
                "El señor de los anillos", 
                Duration.parse("PT2H30M"), 
                LocalDate.parse("2001-12-25"), 
                Genre.FANTASIA,
                1532989000
            )
        );

        // Inserción de películas
        // Estas son sobreescritas cada vez que se ejecuta el programa,
        // ya que aquí no se implementó la lectura de archivos
        do {
            String genreOptions = String.format("%sGéneros%s%n", Utils.PURPLE, Utils.RESET)
            + Utils.optionsString("Acción", "Aventura", "Ciencia Ficción", "Comedia", "Drama", "Fantasía",
            "Musical", "No Ficción / Documental", "Suspenso", "Terror", "Desconocido")
            + String.format("%n%sIngrese el número del género:%s ", Utils.BLUE, Utils.RESET);
            
            String nombre = Keyboard.readString("Nombre de la película (Intro para terminar): ");
            if (nombre.length() == 0) break;
            peliculas.add(
                new Movie(
                    nombre,
                    Keyboard.readDuration("Duración (HH:MM): "),
                    Keyboard.readDate("Fecha de estreno: "),
                    Genre.values()[Keyboard.readInt(1, 11, genreOptions)-1],
                    Keyboard.readDouble("Recaudo: ")
                )
            );
            System.out.printf("%sPelícula agregada con éxito%s%n%n", Utils.GREEN, Utils.RESET);
        } while (true);
        Utils.writeText(peliculas, Utils.PATH + "peliculas.txt");
        System.out.printf("%sPelículas agregadas al archivo%s%n", Utils.GREEN, Utils.RESET);
    }

    private static void testReadText() throws IOException {
        String filePath = Utils.PATH + "peliculas.txt";
        String texto = Utils.readText(filePath);
        System.out.printf("%s%s%s%n%s%n", Utils.PURPLE, filePath, Utils.RESET, texto);

        filePath = Utils.PATH + "futbolistas.txt";
        texto = Utils.readText(filePath);
        System.out.printf("%s%s%s%n%s%n", Utils.PURPLE, filePath, Utils.RESET, texto);

        filePath = "./src/test/java/edu/prog2/AppTest.java";
        texto = Utils.readText(filePath);
        System.out.printf("%s%s%s%n%s%n", Utils.PURPLE, filePath, Utils.RESET, texto);
    }

    private static void testWriteCSV() throws IOException {
        ArrayList<Movie> peliculas = new ArrayList<>();
        peliculas.add(
            new Movie(
                "Harry el sucio", 
                Duration.parse("PT1H50M"), 
                LocalDate.parse("1971-12-23"), 
                Genre.COMEDIA,
                932989142
            )
        );
        peliculas.add(
            new Movie(
                "El padrino", 
                Duration.parse("PT2H10M"), 
                LocalDate.parse("1972-03-24"), 
                Genre.ACCION,
                32989142
            )
        );
        peliculas.add(
            new Movie(
                "Matrix I", 
                Duration.parse("PT1H50M"), 
                LocalDate.parse("1999-05-21"), 
                Genre.CIENCIA_FICCION,
                1632989142
            )
        );
        peliculas.add(
            new Movie(
                "El señor de los anillos", 
                Duration.parse("PT2H30M"), 
                LocalDate.parse("2001-12-25"), 
                Genre.FANTASIA,
                1532989000
            )
        );
        // Línea de inserción
        Utils.writeCSV(peliculas, Utils.PATH + "peliculas.csv");
        System.out.printf("%sHoja de cálculo creada en %speliculas.csv%s%n",
            Utils.GREEN, Utils.PATH, Utils.RESET
        );
    }

    private static void csvToJSON() throws IOException {
        String texto = Utils.readText(Utils.PATH + "peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(",|;|[\n]+|[\r\n]+")) {
            System.out.printf("%sContenido de %speliculas.csv en formato JSON%s%n",
                Utils.PURPLE, Utils.PATH, Utils.RESET
            );
            for (int i = 0; i < 5; i++) sc.next();
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genre genero = Genre.getEnum(sc.next());
                double recaudo = sc.nextDouble();

                Movie p = new Movie(nombre, duracion, fechaEstreno, genero, recaudo);
                System.out.println(p.toJSONObject().toString(2));
            }
        }
    }

    private static void csvToJSON2() throws IOException {
        String texto = Utils.readText(Utils.PATH + "peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(",|;|[\n]+|[\r\n]+")) {
            System.out.printf("%sContenido de %speliculas.csv en formato JSON%s%n",
                Utils.PURPLE, Utils.PATH, Utils.RESET
            );
            for (int i = 0; i < 5; i++) sc.next();
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genre genero = Genre.getEnum(sc.next());
                double recaudo = sc.nextDouble();

                JSONObject json = new JSONObject()
                    .put("nombre", nombre)
                    .put("duracion", duracion)
                    .put("fechaEstreno", fechaEstreno)
                    .put("genero", genero)
                    .put("recaudo", recaudo);

                System.out.println(json.toString(2));
            }
        }
    }

    private static void csvToJSONArray() throws IOException {
        JSONArray array = new JSONArray();

        String texto = Utils.readText(Utils.PATH + "peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(",|;|[\n]+|[\r\n]+")) {
            System.out.printf("%sContenido de %speliculas.csv en formato JSON%s%n",
                Utils.PURPLE, Utils.PATH, Utils.RESET
            );
            for (int i = 0; i < 5; i++) sc.next();
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genre genero = Genre.getEnum(sc.next());
                double recaudo = sc.nextDouble();

                Movie p = new Movie(nombre, duracion, fechaEstreno, genero, recaudo);
                array.put(p.toJSONObject());
            }
        }

        for (int i = 0; i < array.length(); i++) {
            System.out.println(array.get(i));
        }
    }

    private static void testWriteText2() throws IOException {
        String filePath = Utils.PATH + "lorem-ipsum.txt";
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
          + "tempor incididunt ut labore et dolore magna aliqua.\nUt enim ad minim veniam, quis nostrud "
          + "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\nDuis aute irure dolor "
          + "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\nExcepteur "
          + "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est "
          + "laborum.";
        
        // writeText(String content, String fileName) no posee
        // limitaciones con la longitud del contenido
        Utils.writeText(loremIpsum, filePath);
        System.out.printf("%sArchivo %s creado%s%n", Utils.GREEN, filePath, Utils.RESET);
    }

    private static void csvToJSONFile() throws IOException {
        ArrayList<Movie> peliculas = new ArrayList<>();
        String texto = Utils.readText(Utils.PATH + "peliculas.csv");

        try (Scanner sc = new Scanner(texto).useDelimiter(",|;|[\n]+|[\r\n]+")) {
            for (int i = 0; i < 5; i++) sc.next();
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genre genero = Genre.getEnum(sc.next());
                double recaudo = sc.nextDouble();
                
                peliculas.add(new Movie(nombre, duracion, fechaEstreno, genero, recaudo));
            }
        }

        Utils.writeJSON(peliculas, Utils.PATH + "peliculas.json");
        System.out.printf("%sArchivo %speliculas.json creado%s%n", Utils.GREEN, Utils.PATH, Utils.RESET);
    }
}

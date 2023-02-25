package edu.prog2;

import edu.prog2.helpers.*;
import edu.prog2.model.*;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDate;

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
                        //testReadText();
                        break;

                    case 8:
                        //testWriteCSV();
                        break;

                    case 9:
                        //csvToJSON();
                        break;

                    case 10:
                        //csvToJSON2();
                        break;

                    case 11:
                        //csvToJSONArray();
                        break;

                    case 12:
                        //csvToJSONFile();
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
        String options = String.format("%sMenú de opciones:%s\n", Utils.GREEN, Utils.RESET)
          + Utils.optionsString("testFileExists", "testPathExists", "testCreateFolderIfNotExist",
            "testGetPath", "testInitPath", "testWriteText", "testReadText", "testWriteCSV", "csvToJSON",
            "csvToJSON2", "csvToJSONArray", "csvToJSONFile")
          + String.format("%s%2d - Salir%s\n", Utils.RED, 0, Utils.RESET)
          + String.format("\nElija una opción (%s0 para salir%s) > ", Utils.RED, Utils.RESET);

        int option = Keyboard.readInt(options);
        return option;
    }

    private static void testFileExists() {
        String filePath = "./src/main/java/edu/prog2/model/Pelicula.java";
        boolean existe = Utils.fileExists(filePath);
        System.out.println("¿Existe Pelicula.java? = " + existe);

        filePath = "./src/main/java/edu/prog2/model/Movie.java";
        existe = Utils.fileExists(filePath);
        System.out.println("¿Existe Movie.java? = " + existe);

        filePath = "./src/main/java/edu/prog2/helpers/Keyboard.java";
        existe = Utils.fileExists(filePath);
        System.out.println("¿Existe Keyboard.java? = " + existe);

        filePath = "./src/main/java/edu/prog2/helpers/";
        existe = Utils.fileExists(filePath);
        System.out.println("¿Existe helpers? = " + existe);
    }

    private static void testPathExists() {
        String path = "./src/main/java/edu/prog2/model/Pelicula.java";
        boolean existe = Utils.pathExists(path);
        System.out.println("¿Existe Pelicula.java? = " + existe);

        path = "./src/main/java/edu/prog2/model/Movie.java";
        existe = Utils.pathExists(path);
        System.out.println("¿Existe Movie.java? = " + existe);

        path = "./src/main/java/edu/prog2/helpers/Keyboard.java";
        existe = Utils.pathExists(path);
        System.out.println("¿Existe Keyboard.java? = " + existe);

        path = "./src/main/java/edu/prog2/helpers/";
        existe = Utils.pathExists(path);
        System.out.println("¿Existe helpers? = " + existe);
    }

    private static void testCreateFolderIfNotExist() throws Exception {
        String folder = String.format("./data/%s", Utils.getRandomKey(5));
        Utils.createFolderIfNotExist(folder);
        System.out.printf("Carpeta %s creada y lista para ser usada", folder);
    }

    private static void testGetPath() {
        String ruta = Utils.getPath("./src/main/java/edu/prog2/model/Genre.java");
        System.out.println("La ruta padre es: " + ruta);
        ruta = Utils.getPath("./src/main/java/edu/prog2/model/");
        System.out.println("La ruta padre es: " + ruta);
    }

    private static void testInitPath() throws Exception {
        Utils.initPath("./data/varios/prueba.txt");
        Utils.initPath("./data/trazas/prueba.txt");
    }

    private static void testWriteText() throws Exception {
        ArrayList<String> jugadores = new ArrayList<>();
        jugadores.add("Diego Armando Maradona");
        jugadores.add("Lionel Messi");
        jugadores.add("Cristiano Ronaldo");
        jugadores.add("Johan Cruyff");
        jugadores.add("Franz Beckenbauer");

        Utils.writeText(jugadores, "./data/futbolistas.txt");
        
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
        // ––– Línea de inserción –––
        do {
            String genreOptions = String.format("%sGéneros%s\n", Utils.BLUE, Utils.RESET)
            + Utils.optionsString("Acción", "Aventura", "Ciencia Ficción", "Comedia", "Drama", "Fantasía",
            "Musical", "No Ficción / Documental", "Suspenso", "Terror", "Desconocido")
            + String.format("\n%sIngrese el número del género de la película:%s ", Utils.BLUE, Utils.RESET);
            
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
            System.out.printf("%sPelícula agregada con éxito%s\n", Utils.GREEN, Utils.RESET);
        } while (true);
        Utils.writeText(peliculas, "./data/peliculas.txt");
    }
/*
    private static void testReadText() {
        String filePath = "./data/peliculas.txt";
        String texto = Utils.readText(filePath);
        System.out.println(texto);
    }

    private static void testWriteCSV() {
        ArrayList<Pelicula> peliculas = new ArrayList<>();
        peliculas.add(…);
        peliculas.add(…);
        peliculas.add(…);
        peliculas.add(…);
        Utils.writeCSV(peliculas, "./data/peliculas.csv");
    }

    private static void csvToJSON() {
        String texto = Utils.readText("./data/peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genero genero = Genero.getEnum(sc.next());
                double recaudo = sc.nextDouble();

                Pelicula p = new Pelicula(nombre, duracion, fechaEstreno, genero, recaudo);
                System.out.println(p.toJSONObject());
            }
        }
    }

    private static void csvToJSON2() {
        String texto = Utils.readText("./data/peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genero genero = Genero.getEnum(sc.next());
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

    private static void csvToJSONArray() {
        JSONArray array = new JSONArray();

        String texto = Utils.readText("./data/peliculas.csv");
        try (Scanner sc = new Scanner(texto).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genero genero = Genero.getEnum(sc.next());
                double recaudo = sc.nextDouble();

                Pelicula p = new Pelicula(nombre, duracion, fechaEstreno, genero, recaudo);
                array.put(p);
            
            }
        }

        for (int i = 0; i < array.length(); i++) {
            System.out.println(array.get(i));
        }
    }

    private static void csvToJSONFile() {
        ArrayList<Pelicula> peliculas = new ArrayList<>();
        String texto = Utils.readText("./data/peliculas.csv");

        try (Scanner sc = new Scanner(texto).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String nombre = sc.next();
                Duration duracion = Duration.parse(sc.next());
                LocalDate fechaEstreno = LocalDate.parse(sc.next());
                Genero genero = Genero.getEnum(sc.next());
                double recaudo = sc.nextDouble();
                peliculas.add(new Pelicula(nombre, duracion, fechaEstreno, genero, recaudo));
            }
        }

        Utils.writeJSON(peliculas, "./data/peliculas.json");
    }

    private static void test() {
        int test = Utils::getRandom;
    }
*/
}

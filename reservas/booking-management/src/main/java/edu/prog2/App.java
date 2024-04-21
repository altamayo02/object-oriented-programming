package edu.prog2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.*;
import edu.prog2.model.*;
import edu.prog2.services.*;

// TODO - Standardize use of:
// New-lines
// Identifier this
// Variable names
// Multi-lines
// Method structure
// Exceptions

// FIXME - When to use enum.getEnum and enum.valueOf???
// Here: Both retrieve the ENUM CONSTANT
// enum.valueOf does it through its literal (Built-in)
// enum.getEnum does it through its value attribute (Custom)

// TODO - Make sure object members are recovered with getJSONObject(), and not getString()
public class App {
    static UserService usuarios;
    static AircraftService aviones;
    static JourneyService trayectos;
    static FlightService vuelos;
    static SeatService sillas;
    static BookingService reservas;

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        try {
            initSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            try {
                int option = readChoice();
                switch (option) {
                    case 1:
                        crearUsuario();
                        break;

                    case 2:
                        crearAviones();
                        break;

                    case 3:
                        crearTrayectos();
                        break;

                    case 4:
                        crearVuelos();
                        break;

                    case 5:
                        crearReservas();
                        break;

                    case 6:
                        listarUsuarios();
                        break;

                    case 7:
                        listarAviones();
                        break;

                    case 8:
                        listarTrayectos();
                        break;

                    case 9:
                        listarVuelos();
                        break;
                    
                    case 10:
                        listarReservas();
                        break;

                    case 11:
                        actualizarUsuario();
                        break;

                    case 12:
                        break;

                    case 13:
                        break;

                    case 14:
                        actualizarVuelos();
                        break;

                    case 15:
                        break;

                    case 16:
                        removerUsuario();
                        break;

                    case 17:
                        removerAvion();
                        break;

                    case 18:
                        break;

                    case 19:
                        break;

                    case 21:
                        buscarVuelos();
                        break;
                    
                    case 22:
                        buscarSillas();
                        break;

                    case 23:
                        listarVuelos2();
                        break;

                    case 24:
                        listarTrayectos2();
                        break;
                    
                    case 25:
                        buscarPorIndice();
                        break;

                    case 26:
                        buscarPorIndice2();
                        break;

                    case 27:
                        buscarPorId();
                        break;
                    
                    case 28:
                        buscarPorId2();
                        break;

                    case 29:
                        usuarios.authenticate(new JSONObject()
                            .put("perfil", "Kandulona")
                            .put("contrasenia", "gothGirlsAreMyPassion")
                        );
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
        } while (true);
    }

    private static void initSelf() throws Exception {
        System.out.print(Utils.CLEAR);
        Utils.setLocale("es", "CO", "Latn");
        usuarios = new UserService();
        aviones = new AircraftService();
        trayectos = new JourneyService();
        vuelos = new FlightService(trayectos, aviones);
        sillas = new SeatService(aviones);
        reservas = new BookingService(usuarios, vuelos, sillas);

        /* get("index", (req, res) -> "<body style='background-color: lavender; color: turquoise'>");
        get("index", (req, res) -> "<h1>this is a test</h1>"); */
    }

    private static void exit() {
        System.out.print(Utils.CLEAR);
        // Limpia el caché de escritura
        System.out.flush();
        System.exit(0);
    }

    private static int readChoice() {
        String options = String.format("%sMenú de opciones:%s%n", Utils.PURPLE, Utils.RESET)
            + Utils.listString(
                "Crear usuarios", "Crear aviones con sillas", "Crear trayectos", "Crear vuelos", "Crear reservas",
                "Listar usuarios", "Listar aviones con sillas", "Listar trayectos", "Listar vuelos", "...",
                "Actualizar usuario", "...", "...", "Actualizar vuelo", "...",
                "Eliminar usuario", "Eliminar avión", "...", "...", "...",
                "Buscar vuelos", "Buscar sillas", "Listar vuelos 2", "Listar trayectos 2",
                "Buscar por índice", "Buscar por índice 2", "Buscar por ID", "Buscar por ID 2",
                "Listar sillas disponibles"
            )
            + String.format("%s%3d - Salir%s%n%n", Utils.RED, 0, Utils.RESET)
            + String.format("Elija una opción (%s0 para salir%s) > ", Utils.RED, Utils.RESET);

        int choice = Keyboard.readInt(options);
        System.out.println();
        return choice;
    }

    // Caso 1
    private static void crearUsuario() throws Exception {
        System.out.printf("%sIngreso de usuarios%s%n%n", Utils.PURPLE, Utils.RESET);

        User usuario;
        do {
            String id = Keyboard.readString(1, 15, "Identificación (Intro para terminar): ");

            if (id.length() == 0) {
                System.out.println();
                return;
            }

            String nombres = Keyboard.readString(1, 20, "Nombres: ");
            String apellidos = Keyboard.readString(1, 25, "Apellidos: ");
            String perfil = Keyboard.readString(1, 20, "Nombre de perfil de usuario: ");
            String contrasenia = Keyboard.readString(1, 20, "Contraseña: ");
            UserType tipo = Keyboard.readEnum(UserType.class, "Tipo de usuario: ");

            usuario = new User(id, nombres, apellidos, perfil, contrasenia, tipo);
            boolean ok = usuarios.add(usuario);

            if (ok) System.out.printf(
                "%sSe agregó el usuario con identificación %s%s%s%n%n",
                Utils.GREEN, Utils.CYAN, id, Utils.RESET
            );
        } while (true);
    }

    // Caso 6
    private static void listarUsuarios() {
        System.out.println(Utils.YELLOW + "-".repeat(138) + Utils.RESET);
        System.out.printf(
            "%s" + User.formatSpec() + "%s%n",
            Utils.PURPLE, "ID", "NOMBRES", "APELLIDOS", "PERFIL", "CONTRASEÑA", "TIPO", Utils.RESET
        );
        System.out.println(Utils.YELLOW + "-".repeat(138) + Utils.RESET);

        for (User u : usuarios.getList()) {
            System.out.println(u);
        }
        System.out.println(Utils.YELLOW + "-".repeat(138) + Utils.RESET + "\n");
    }

    // Caso 11
    private static void actualizarUsuario() throws Exception {
        String id = Keyboard.readString("Identificación del usuario a modificar: ");
    
        JSONObject json = new JSONObject()
            .put("id", "no interesa")
            .put("nombres", Keyboard.readString("Nuevo nombre: "))
            .put("apellidos", Keyboard.readString("Nuevos apellidos: "))
            .put("perfil", Keyboard.readString("Nuevo nombre de perfil de usuario: "))
            .put("contrasenia", Keyboard.readString("Nueva contraseña: "))
            .put("tipo", Keyboard.readEnum(UserType.class, "Nuevo tipo: ").toString());
    
        System.out.printf("%sNuevos datos del usuario %s:%s%n", Utils.PURPLE, id, Utils.RESET);
        System.out.println(Utils.CYAN + usuarios.set(id, json).toString(2) + Utils.RESET + "\n");
    }

    // Caso 16
    private static void removerUsuario() throws Exception {
        String id = Keyboard.readString("Identificación del usuario a eliminar: ");
        usuarios.remove(id);
        System.out.println("Usuario eliminado");
    }

    private static User elegirUsuario() {
        User usuario;
        int i;
        int size = usuarios.getList().size();

        do {
            System.out.printf("%sUsuarios elegibles:%s%n", Utils.PURPLE, Utils.RESET);

            for (i = 0; i < size; i++) {
                usuario = usuarios.get(i);
                System.out.printf(
                    "%2d - %s - %s %s%n",
                    (i + 1), usuario.getId(), usuario.getNombres(), usuario.getApellidos()
                );
            }

            i = Keyboard.readInt(String.format(
                "Ingrese un índice entre 1 y %d (0 para terminar): ", size
            ));
        } while (i < 0 || i > size);

        return (i == 0) ? null : usuarios.get(i - 1);
    }

    // Caso 2
    private static void crearAviones() throws Exception {
        System.out.printf("%sIngreso de aviones%s%n%n", Utils.PURPLE, Utils.RESET);

        do {
            String matricula = Keyboard.readString(1, 10, "Matrícula (Intro para terminar) > ");

            if (matricula.length() == 0) {
                System.out.println();
                return;
            }

            String modelo = Keyboard.readString(1, 20, "Modelo: ");
            int totalSillas = Keyboard.readInt("Cantidad de sillas: ");
            int filasEjecutivas = Keyboard.readInt("Cantidad de filas ejecutivas: ");

            boolean ok = aviones.add(new Aircraft(matricula, modelo));

            if (ok) {
                System.out.printf(
                    "%sSe agregó el avión con matrícula %s%s%s%n",
                    Utils.GREEN, Utils.CYAN, matricula, Utils.RESET
                );
                sillas.create(matricula, filasEjecutivas, totalSillas);
                System.out.printf(
                    "%sSe agregaron las sillas al avión %s%s%s%n%n",
                    Utils.GREEN, Utils.CYAN, matricula, Utils.RESET
                );
            }
        } while (true);
    }

    // Caso 7
    private static void listarAviones() {
        System.out.println(Utils.YELLOW + "-".repeat(32) + Utils.RESET);
        System.out.printf(
                "%s" + Aircraft.formatSpec() + "%s%n",
                Utils.PURPLE, "MATRÍCULA", "MODELO", Utils.RESET);
        System.out.println(Utils.YELLOW + "-".repeat(32) + Utils.RESET);

        for (Aircraft a : aviones.getList()) {
            System.out.println(a);
        }
        System.out.println(Utils.YELLOW + "-".repeat(32) + Utils.RESET + "\n");
    }

    // Caso 17
    private static void removerAvion() throws Exception {
        System.out.printf("%sRemover aviones%s%n%n", Utils.PURPLE, Utils.RESET);
        
        System.out.println(Utils.listString(aviones.getList()));
        int i = Keyboard.readInt("Seleccione el índice del avión a eliminar > ");
        if (i < 1 || i >= aviones.getList().size()) System.out.println("Índice fuera de rango.");
        else {
            sillas.removeAll(aviones.get(i).getPlaca());
            System.out.printf("%sSe ha eliminado el avión con éxito.%s%n%n", Utils.GREEN, Utils.RESET);
        }
    }

    private static Aircraft elegirAvion() {
        Aircraft avion;
        int i;
        int size = aviones.getList().size();

        do {
            System.out.printf("%sAviones elegibles:%s%n", Utils.PURPLE, Utils.RESET);

            for (i = 0; i < size; i++) {
                avion = aviones.get(i);
                System.out.printf(
                        "%2d - %s %s%n", (i + 1), avion.getPlaca(), avion.getModelo());
            }

            i = Keyboard.readInt(String.format(
                "Ingrese un índice entre 1 y %d (0 para terminar): ", size
            ));
        } while (i < 0 || i > size);

        return (i == 0) ? null : aviones.get(i - 1);
    }

    // Caso 3
    private static void crearTrayectos() throws Exception {
        System.out.printf("%sIngreso de trayectos%s%n%n", Utils.PURPLE, Utils.RESET);

        Journey trayecto;
        do {
            String origen = Keyboard.readString(1, 15, "Origen (Intro para terminar) > ");

            if (origen.length() == 0) {
                System.out.println();
                return;
            }

            String id = "T-" + Utils.getRandomKey(5);
            String destino = Keyboard.readString(1, 15, "Destino: ");
            double costo = Keyboard.readDouble("Costo: ");
            Duration duracion = Keyboard.readDuration("Duración: ");

            trayecto = new Journey(id, origen, destino, costo, duracion);
            boolean ok = trayectos.add(trayecto);

            if (ok) System.out.printf(
                "%sSe agregó el trayecto con identificación %s%s%s%n%n",
                Utils.GREEN, Utils.CYAN, id, Utils.RESET
            );
        } while (true);
    }

    // Caso 8
    private static void listarTrayectos() {
        System.out.println(Utils.YELLOW + "-".repeat(70) + Utils.RESET);
        System.out.printf(
                "%s" + Journey.formatSpec(true) + "%s%n",
                Utils.PURPLE, "ID", "ORIGEN", "DESTINO", "COSTO", "DURACIÓN", Utils.RESET);
        System.out.println(Utils.YELLOW + "-".repeat(70) + Utils.RESET);

        for (Journey t : trayectos.getList()) {
            System.out.println(t);
        }
        System.out.println(Utils.YELLOW + "-".repeat(70) + Utils.RESET + "\n");
    }

    private static Journey elegirTrayecto() {
        Journey trayecto;
        int i;
        int size = trayectos.getList().size();

        do {
            System.out.printf("%sTrayectos elegibles:%s%n", Utils.PURPLE, Utils.RESET);

            for (i = 0; i < size; i++) {
                trayecto = trayectos.get(i);
                System.out.printf(
                        "%2d - %s - %s (%.2f)%n", (i + 1),
                        trayecto.getOrigen(), trayecto.getDestino(), trayecto.getCosto());
            }

            i = Keyboard.readInt(String.format(
                "Ingrese un índice entre 1 y %d (0 para terminar): ", size
            ));
        } while (i < 0 || i > size);

        return (i == 0) ? null : trayectos.get(i - 1);
    }

    // Caso 4
    private static void crearVuelos() throws Exception {
        System.out.printf("%sIngreso de vuelos%s%n", Utils.PURPLE, Utils.RESET);
        System.out.printf(
                "%sElija 0 en trayectos o aviones para terminar%s%n%n",
                Utils.CYAN, Utils.RESET);

        do {
            String id = "V-" + Utils.getRandomKey(5);

            Journey trayecto = elegirTrayecto();
            if (trayecto == null) {
                return;
            }

            Aircraft avion = elegirAvion();
            if (avion == null) {
                return;
            }

            LocalDateTime desde = LocalDateTime.now();
            LocalDateTime hasta = LocalDateTime.now().plusYears(1);

            String mensaje = String.format(
                    "Fecha y hora entre %s y %s > ", Utils.dateTimeString(desde), Utils.dateTimeString(hasta));
            LocalDateTime fechaHora = Keyboard.readDateTime(
                    Utils.dateTimeString(desde), Utils.dateTimeString(hasta), mensaje);

            boolean ok = vuelos.add(new Flight(id, fechaHora, trayecto, avion, FlightState.PROGRAMADO));

            if (ok) System.out.printf(
                "%sSe agregó el vuelo con identificación %s%s%s%n%n",
                Utils.GREEN, Utils.CYAN, id, Utils.RESET
            );
        } while (true);
    }

    // Caso 9
    private static void listarVuelos() {
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET);
        System.out.printf(
            "%s" + Flight.formatSpec(true) + "%s%n",
            Utils.PURPLE, "ID", "FECHA", "AVIÓN", "ORIGEN",
            "DESTINO", "TIEMPO", "VALOR", "ESTADO", Utils.RESET
        );
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET);

        for (Flight v : vuelos.getList()) {
            System.out.println(v);
        }
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET + "\n");
    }

    // Caso 14
    private static void actualizarVuelos() throws Exception {
        vuelos.update();
        System.out.printf("%sLos vuelos se han actualizado.%s%n%n", Utils.GREEN, Utils.RESET);
    }


    // Caso 21
    private static void buscarVuelos() throws Exception {
        LocalDateTime fechaHora = Keyboard.readDateTime(
            "Ingrese la fecha desde la cual desea buscar vuelos: "
        );
        String origen = Keyboard.readString("Ingrese el origen de los vuelos: ");
        String destino = Keyboard.readString("Ingrese el destino de los vuelos: ");

        JSONArray jsonResultados = vuelos.select(
            String.format("fechaHora=%s&origen=%s&destino=%s", fechaHora, origen, destino)
        );

        System.out.printf(
            "%sEstos son los vuelos registrados desde %s hasta %s, para después de la fecha %s:%s%n%n",
            Utils.GREEN, origen, destino, Utils.dateTimeString(fechaHora), Utils.RESET
        );

        ArrayList<Flight> resultados = new ArrayList<>();
        for (int i = 0; i < jsonResultados.length(); i++) {
            resultados.add(new Flight(jsonResultados.getJSONObject(i)));
        }
        // Utils.writeData(resultados, Utils.PATH + "vuelosBusqueda");
        listarVuelos(resultados);
    }

    private static void listarVuelos(ArrayList<Flight> vuelos) {
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET);
        System.out.printf(
            "%s" + Flight.formatSpec(true) + "%s%n",
            Utils.PURPLE, "ID", "FECHA", "AVIÓN", "ORIGEN",
            "DESTINO", "TIEMPO", "VALOR", "ESTADO", Utils.RESET
        );
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET);

        for (Flight v : vuelos) {
            System.out.println(v);
        }
        System.out.println(Utils.YELLOW + "-".repeat(98) + Utils.RESET + "\n");
    }

    private static Flight elegirVuelo() {
        Flight vuelo;
        int i;
        int size = vuelos.getList().size();

        do {
            System.out.printf("%sVuelos elegibles:%s%n", Utils.PURPLE, Utils.RESET);

            for (i = 0; i < size; i++) {
                vuelo = vuelos.get(i);
                System.out.printf(
                        "%2d - %s - %s %s - %s ($%s)%n", (i + 1),
                        vuelo.strFechaHora(), vuelo.getAvion().getPlaca(), vuelo.getTrayecto().getOrigen(),
                        vuelo.getTrayecto().getDestino(), vuelo.getTrayecto().getCosto());
            }

            i = Keyboard.readInt(String.format(
                "Ingrese un índice entre 1 y %d: ", size
            ));
        } while (i < 0 || i > size);

        return (i == 0) ? null : vuelos.get(i - 1);
    }

    // Caso 22
    private static void buscarSillas() throws Exception {
        String placa = Keyboard.readString(
            "Ingrese la matrícula del avión para listar sus sillas > "
        );
        System.out.println(
            "\n" + Utils.CYAN + sillas.select(placa).toString(2) + Utils.RESET + "\n"
        );
    }

    // Caso 23
    private static void listarVuelos2() throws Exception {
        String str = sillas.aircraftsWithNumberSeats().toString(2);
        //get("index", (req, res) -> Utils.formatHtml("h1", str));
        System.out.println(str);
    }

    // Caso 24
    private static void listarTrayectos2() {
        for (Journey t : vuelos.getTrayectos().getList()) {
            System.out.println(t);
        }
        System.out.println();
    }

    // Caso 25
    private static void buscarPorIndice() {
        int i = Keyboard.readInt("Índice del usuario a buscar: ");
        User usuario = usuarios.get(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, usuario.toJSONObject().toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del trayecto a buscar: ");
        Journey trayecto = trayectos.get(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, trayecto.toJSONObject().toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del avión a buscar: ");
        Aircraft avion = aviones.get(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, avion.toJSONObject().toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del vuelo a buscar: ");
        Flight vuelo = vuelos.get(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, vuelo.toJSONObject().toString(2), Utils.RESET);
    }

    // Caso 26
    private static void buscarPorId() throws Exception {
        String id = Keyboard.readString("Identificación del usuario: ");
        User usuario = usuarios.get(new User(id));
        System.out.printf("%s%s%s%n%n", Utils.CYAN, usuario.toJSONObject().toString(2), Utils.RESET);

        id = Keyboard.readString("Identificación del trayecto: ");
        Journey trayecto = trayectos.get(new Journey(id));
        System.out.printf("%s%s%s%n%n", Utils.CYAN, trayecto.toJSONObject().toString(2), Utils.RESET);

        id = Keyboard.readString("Identificación del avión: ");
        Aircraft avion = aviones.get(new Aircraft(id));
        System.out.printf("%s%s%s%n%n", Utils.CYAN, avion.toJSONObject().toString(2), Utils.RESET);

        id = Keyboard.readString("Identificación del vuelo: ");
        Flight vuelo = vuelos.get(new Flight(id));
        System.out.printf("%s%s%s%n%n", Utils.CYAN, vuelo.toJSONObject().toString(2), Utils.RESET);
    }

    // Caso 27
    private static void buscarPorIndice2() {
        int i = Keyboard.readInt("Índice del usuario a buscar: ");
        JSONObject usuario = usuarios.getJSON(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, usuario.toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del trayecto a buscar: ");
        JSONObject trayecto = trayectos.getJSON(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, trayecto.toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del avión a buscar: ");
        JSONObject avion = aviones.getJSON(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, avion.toString(2), Utils.RESET);

        i = Keyboard.readInt("Índice del vuelo a buscar: ");
        JSONObject vuelo = vuelos.getJSON(i);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, vuelo.toString(2), Utils.RESET);
    }

    // Caso 28
    private static void buscarPorId2() throws Exception {
        String id = Keyboard.readString("Identificación del usuario: ");
        JSONObject usuario = usuarios.get(id);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, usuario.toString(2), Utils.RESET);
        
        id = Keyboard.readString("Identificación del trayecto: ");
        JSONObject trayecto = trayectos.get(id);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, trayecto.toString(2), Utils.RESET);

        
        id = Keyboard.readString("Identificación del avión: ");
        JSONObject avion = aviones.get(id);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, avion.toString(2), Utils.RESET);

        
        id = Keyboard.readString("Identificación del vuelo: ");
        JSONObject vuelo = vuelos.get(id);
        System.out.printf("%s%s%s%n%n", Utils.CYAN, vuelo.toString(2), Utils.RESET);

        System.out.println(Utils.CYAN + usuarios.getJSONArray().toString(2) + Utils.RESET + "\n");
        System.out.println(Utils.CYAN + trayectos.getJSONArray().toString(2) + Utils.RESET + "\n");
        System.out.println(Utils.CYAN + aviones.getJSONArray().toString(2) + Utils.RESET + "\n");
        System.out.println(Utils.CYAN + vuelos.getJSONArray().toString(2) + Utils.RESET + "\n");
    }

    private static List<Seat> listarSillasDisponibles(Flight vuelo) throws Exception {
        List<Seat> disponibles = reservas.availableSeatsOnFlight(vuelo);

        System.out.printf("Listado de sillas disponibles en el vuelo %s", vuelo.getId());

        for (int i = 0; i < disponibles.size(); i++) {
            if (i % 8 == 0) {
                System.out.println();
            }

            Seat s = disponibles.get(i);
            String tipo = s instanceof ExecutiveSeat ? "Ejec." : "Econ.";
            System.out.printf(
                "%s%4d%s. %s (%s)", Utils.CYAN, i + 1, Utils.RESET, s.getPosicion(), tipo
            );
        }
        System.out.println("\n");

        return disponibles;
    }

    private static Seat elegirSillaDisponible(Flight vuelo) throws Exception {
        List<Seat> sillasDisponibles = listarSillasDisponibles(vuelo);
    
        int i = Keyboard.readInt(1, sillasDisponibles.size(), String.format(
            "Elija un índice entre 1 y %s > ", sillasDisponibles.size()
        ));
        if (i == 0) return null;
        
        Seat silla = sillasDisponibles.get(i - 1);
        silla.setDisponible(false);
        
        if (silla instanceof ExecutiveSeat) {
            ExecutiveSeat tempSilla = (ExecutiveSeat) silla;
            tempSilla.setMenu(Keyboard.readEnum(Menu.class, "Seleccione el menú: "));
            tempSilla.setLicor(Keyboard.readEnum(Liquor.class, "Seleccione el licor: "));
            silla = tempSilla;
        }
    
        return silla;
    }

    private static void crearReservas() throws Exception {
        do {
            User usuario = elegirUsuario();
            if (usuario == null) return;

            Flight vuelo;
            do {
                vuelo = elegirVuelo();
                if (vuelo != null) break;
                System.out.printf("%sValor erróneo.%s%n%n", Utils.RED, Utils.RESET);
            } while (true);

            Seat silla;
            do {
                silla = elegirSillaDisponible(vuelo);
                if (silla != null) break;
                System.out.printf("%sValor erróneo.%s%n%n", Utils.RED, Utils.RESET);
            } while (true);

            String id = "R-" + Utils.getRandomKey(5);
            Booking reserva = new Booking(id, usuario, vuelo, silla);
            reservas.add(reserva);
            System.out.println("\nSe ha registrado la reserva con ID " + id);
            System.out.println();
        } while (true);
    }

    private static void listarReservas() {
        for (Booking r : reservas.getList()) {
            System.out.printf("%s%s%s%n", Utils.YELLOW, "-".repeat(100), Utils.RESET);
            System.out.println(r);
        }
    }

    /* private static void probarConstructores() throws Exception {
        // Mutadores
        User u1 = new User();
        u1.setId("U-" + Utils.getRandomKey(5));
        u1.setNombres("Michael");
        u1.setApellidos("Moorcock");

        // Constructor Copia
        User u2 = new User(u1);
        usuarios.add(u1);
        usuarios.add(u2);

        System.out.print(Utils.CYAN);
        System.out.println("U1: " + u1);
        System.out.println("U2: " + u2);
        System.out.println("U1 == U2: " + (u1 == u2));
        System.out.println("U1 == U2: " + (u1.equals(u2)));
        System.out.println(
            "usuarios.getList().get(0) == usuarios.getList().get(1): " +
            (usuarios.getList().get(0) == usuarios.getList().get(1))
        );
        System.out.println(Utils.RESET);
    } */

    /* private static void probarReferencia() throws Exception {
        // Constructor Personalizado
        User u1 = new User("U-" + Utils.getRandomKey(5));
        u1.setNombres("Michael");
        u1.setApellidos("Moorcock");
        usuarios.add(u1);

        // Nueva referencia que apunta a la misma dirección de memoria
        User u2 = u1;
        usuarios.add(u2);

        u2.setId("U-" + Utils.getRandomKey(5));
        u1.setNombres("Louise");
        u2.setApellidos("Cooper");

        System.out.print(Utils.CYAN);
        System.out.println(u1);
        System.out.println(u2);
        System.out.println("U1 == U2: " + (u1 == u2));
        System.out.println("U1 == U2: " + (u1.equals(u2)));
        System.out.println(Utils.YELLOW + "-".repeat(50) + Utils.RESET);
        listarUsuarios();
    } */

    /* private static void probarAsociaciones() {
        Journey t = trayectos.get(0);
        t.setCosto(199999);
        System.out.printf("%sDatos del trayecto:%s%n", Utils.PURPLE, Utils.RESET);
        System.out.println(trayectos.get(0).toJSONObject().toString(2));
        System.out.println(Utils.YELLOW + "-".repeat(60) + Utils.RESET);

        Airplane v = aviones.get(0);
        v.setModelo("Boeing 787-10 Dreamliner");
        System.out.printf("%sDatos del avión:%s%n", Utils.PURPLE, Utils.RESET);
        System.out.println(aviones.get(0).toJSONObject().toString(2));
        System.out.println(Utils.YELLOW + "-".repeat(60) + Utils.RESET);

        System.out.printf("%sDatos del vuelo:%s%n", Utils.PURPLE, Utils.RESET);
        System.out.println(vuelos.get(0).toJSONObject().toString(2) + "\n");
    } */
}

package edu.prog2;

import edu.prog2.helpers.*;
// ¿Por qué no se importan desde Utils/Keyboard?
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            try {
                int option = readOption();
                switch (option) {
                    case 1:
                        series();
                        break;

                    case 2:
                        averageAges();
                        break;

                    case 3:
                        inputHobbies();
                        break;

                    case 4:
                        toCentury();
                        break;

                    case 5:
                        averageWeights();
                        break;
                    
                    case 6:
                        testBoolean();
                        break;
                    
                    case 7:
                        testDate();
                        break;

                    case 8:
                        testDateTime();

                    case 9:

                    case 10:

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

    private static void initialize() {
        System.out.print(Utils.CLEAR);
        Locale.setDefault(new Locale("es_CO"));
    }

    private static void exit() {
        System.out.print(Utils.CLEAR);
        System.out.flush();
        System.exit(0);
    }

    private static int readOption() {
        String options = String.format(
            "\n%sMenú de opciones:%s\n", Utils.GREEN, Utils.RESET
        ) + "  1 - Serie de un rango      5 - Promediar pesos\n"
          + "  2 - Promediar edades       6 - Opciones de falso o verdadero\n"
          + "  3 - Ingresar pasatiempos   7 - Ingreso de fechas\n"
          + "  4 - Convertir a siglos     8 - ...\n"
          + String.format("  %s0 - Salir%s\n", Utils.RED, Utils.RESET)
          + String.format(
             "\nElija una opción (%s0 para salir%s) > ", 
             Utils.RED, Utils.RESET
          );

        int option = Keyboard.readInt(options);
        return option;
    }
    
    // Generar Serie
    private static void series() {
        int start = Keyboard.readInt("Valor inicial de la serie: ");
        int end = Keyboard.readInt("Valor final de la serie: ");
        if (start > end) {
            int tempStart = start;
            start = end;
            end = tempStart;
        }
        
        System.out.printf("La serie que comienza en %d y termina en %d es: %n", start, end);
        for (int i = start; i <= end; i++) {
            System.out.printf("%d; ", i);
        }
    }

    // Promediar Edades
    private static void averageAges() {
        int numKids = 0;
        float avgAge = 0;
        int age;

        System.out.println();
        do {
            age = Keyboard.readInt(1, 5, "Edad del niño (1-5, 0 para terminar): ");
            avgAge += age;
            numKids++;
        } while (age != 0);
        // Edades sumadas / Número de niños (Ingresar el 0 no cuenta como niño)
        avgAge /= numKids - 1;
        System.out.printf("El promedio de edad de los niños es %.2f años.\n", avgAge);
    }

    // Ingresar Pasatiempos
    private static void inputHobbies() {
        String hobbies = Keyboard.readString(
          10, 100, "\nPasatiempos preferidos (10-100 caracteres, opcional): "
        );
        if (hobbies.length() != 0) {
            System.out.printf("Pasatiempos ingresados: %s\n", hobbies);
        } else {
            System.out.println("No ingreso ningún pasatiempo");
        }
    }

    // Convertir a Siglo
    private static void toCentury() {
        int years = Keyboard.readInt(40000, 999999, "\nAños (40000-999999, 0 para terminar): ");
        float centuries = years / (float)100;
        System.out.printf("%d años equivalen a %.1f siglos.\n", years, centuries);
    }

    // Promediar pesos
    private static void averageWeights() {
        int numPeople = 0;
        double avgWeight = 0;
        double weight;

        System.out.println();
        do {
            weight = Keyboard.readDouble(10, 150, "Peso de la persona (10.0-150.0 Kg, 0 para terminar): ");
            avgWeight += weight;
            numPeople++;
        } while (weight != 0);
        // Pesos sumados / Número de personas (Ingresar el 0 no cuenta como persona)
        avgWeight = avgWeight / (numPeople - 1);
        System.out.printf("El promedio de peso de las personas es %.2f Kilogramos.\n", avgWeight);
    }

    // Probar Booleano
    private static void testBoolean() {
        boolean ok = Keyboard.readBoolean("\nIndique si funciona o no: ");
        System.out.printf("Funciona; valor: %s\n", ok ? "VERDADERO" : "FALSO");
    }

    // Probar Fecha
    private static void testDate() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String minDate = String.format("%d-01-01", year - 40);
        String maxDate = String.format("%d-12-31", year - 18);

        System.out.println("\nFecha actual: " + today);
        System.out.printf("Se aceptan nacidos entre %s y %s\n", minDate, maxDate);
        LocalDate date = Keyboard.readDate(minDate, maxDate, "Fecha de nacimiento (AAAA-MM-DD): ");
        System.out.println("Fecha de nacimiento válida: " + date);
    }

    // Probar Fecha y Hora
    private static void testDateTime() {
        LocalDateTime dateTime = Keyboard.readDateTime("Fecha y hora de ingreso: ");
        System.out.printf("Fecha y hora: %s", dateTime);
        
    }
}
package edu.prog2.helpers;

import java.io.Console;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.util.EnumSet;

public class Keyboard {
    private static Console con = System.console();
    public static Scanner sc = new Scanner(con.reader()).useDelimiter("[\n]+|[\r\n]+");

    // TODO - Ask if it's possible to prevent the user from spamming \n (\u0008 backspace?)
    // TODO - Specify required lengths in error messages

    private Keyboard() {}

    public static String readString(String message) {
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
        return sc.nextLine();
    }

    public static String readString(int from, int to, String message) {
        String value;
        if (from > to) {
            int tempFrom = from;
            from = to;
            to = tempFrom;
        }
        
        do {
            value = readString(message);
            // Si el valor está por fuera de la longitud
            if (value.length() != 0 && (value.length() < from || value.length() > to)) {
                System.out.printf("%sLongitud fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);

        return value;
    }

    public static int readInt(String message) {
        boolean ok;
        int value = Integer.MIN_VALUE;
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                value = sc.nextInt();
            } catch (InputMismatchException ime) {
                ok = false;
                System.out.printf("%sValor erróneo.%s%n%s", Utils.RED, Utils.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
    
        return value;
    }

    public static int readInt(int from, int to, String message) {
        int value;
        if (from > to) {
            int tempFrom = from;
            from = to;
            to = tempFrom;
        }

        do {
            value = readInt(message);
            // Si el valor está por fuera del rango
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);

        return value;
    }

    public static long readLong(String message) {
        boolean ok;
        long value = Long.MIN_VALUE;
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                value = sc.nextLong();
            } catch (InputMismatchException ime) {
                ok = false;
                System.out.printf("%sValor erróneo.%s%n%s", Utils.RED, Utils.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
    
        return value;
    }

    public static long readLong(long from, long to, String message) {
        long value;
        if (from > to) {
            long tempFrom = from;
            from = to;
            to = tempFrom;
        }

        do {
            value = readLong(message);
            // Si el valor está por fuera del rango
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);

        return value;
    }

    public static double readDouble(String message) {
        boolean ok;
        double value = Double.NaN;
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                value = sc.nextDouble();
            } catch (InputMismatchException ime) {
                ok = false;
                System.out.printf("%sValor erróneo.%s%n%s", Utils.RED, Utils.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
    
        return value;
    }
    
    public static double readDouble(double from, double to, String message) {
        double value;
        if (from > to) {
            double tempFrom = from;
            from = to;
            to = tempFrom;
        }

        do {
            value = readDouble(message);
            // Si el valor está por fuera del rango
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);

        return value;
    }

    public static boolean readBoolean(String message) {
        boolean ok;
        boolean value = false;
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                // Encontrar sinónimos de los valores booleanos, asegurándose de que no sean subcadenas
                String str = ' ' + sc.nextLine().toLowerCase().trim() + ' ';
                if (" si s true t yes y ".contains(str)) {
                    value = true;
                } else if (" no n false f not ".contains(str)) {
                    value = false;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException ime) {
                ok = false;
                System.out.printf(
                   "%sSe esperaba [si|s|true|t|yes|y|no|not|n|false|f]%s%n%s", 
                   Utils.RED, Utils.RESET, message
                );
            }
        } while (!ok);
    
        return value;
    }

    public static LocalDate readDate(String message) {
        boolean ok;
        LocalDate date = LocalDate.now();
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                String strDate = sc.nextLine().trim().toLowerCase();
                // Conservar el valor LocalDate.now() si se reciben estos Strings
                if (!"hoy|now".contains(strDate)) {
                    date = LocalDate.parse(strDate);
                }
            } catch (DateTimeParseException dtpe) {
                ok = false;
                System.out.printf(
                   "%sFecha errónea.%s%n%s", Utils.RED, Utils.RESET, message
                );
            }
        } while (!ok);
    
        return date;
    }

    public static LocalDate readDate(String from, String to, String message) {
        LocalDate date;
        LocalDate dateFrom = LocalDate.parse(from);
        LocalDate dateTo = LocalDate.parse(to);
        if (dateFrom.isAfter(dateTo)) {
            LocalDate tempFrom = dateFrom;
            dateFrom = dateTo;
            dateTo = tempFrom;
        }

        do {
            date = readDate(message);
            // Si el valor está por fuera del rango
            if (date.isBefore(dateFrom) || date.isAfter(dateTo)) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);
        
        return date;
    }

    public static LocalDateTime readDateTime(String message) {
        boolean ok;
        LocalDateTime dateTime = LocalDateTime.now();
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);

        do {
            try {
                ok = true;
                String strDateTime = sc.nextLine().trim().toLowerCase();
                // Conservar el valor LocalDateTime.now() si se reciben estos Strings
                if (!"ahora|now".contains(strDateTime)) {
                    dateTime = LocalDateTime.parse(strDateTime.replace(" ", "T"));
                }
            } catch (DateTimeParseException dtpe) {
                ok = false;
                System.out.printf("%sFecha u hora erróneas.%n", Utils.RED);
                System.out.printf("Formato esperado: yyyy-MM-dd HH:mm%s%n%s", Utils.RESET, message);
            }
        } while (!ok);

        return dateTime;
    }

    public static LocalDateTime readDateTime(String from, String to, String message) {
        LocalDateTime dateTime;
        LocalDateTime dateTimeFrom = LocalDateTime.parse(from.replace(" ", "T"));
        LocalDateTime dateTimeTo = LocalDateTime.parse(to.replace(" ", "T"));
        if (dateTimeFrom.isAfter(dateTimeTo)) {
            LocalDateTime tempFrom = dateTimeFrom;
            dateTimeFrom = dateTimeTo;
            dateTimeTo = tempFrom;
        }

        do {
            dateTime = readDateTime(message);
            // Si el valor está por fuera del rango
            if (dateTime.isBefore(dateTimeFrom) || dateTime.isAfter(dateTimeTo)) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);

        return dateTime;
    }

    private static Duration toDuration(String durationString) {
        String[] arrDuration = durationString.trim().split(":");
        if (arrDuration.length != 2) {
            throw new IllegalArgumentException("Se esperaba HH:mm.");
        }
    
        return Duration.parse(
            String.format("PT%sH%sM", arrDuration[0].trim(), arrDuration[1].trim())
        );
    }

    public static Duration readDuration(String message) {
        boolean ok;
        Duration duration = Duration.ZERO;
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        System.out.print(message);
    
        do {
            try {
                ok = true;
                String strDuration = sc.nextLine();
                duration = toDuration(strDuration);
            } catch (Exception e) {
                ok = false;
                System.out.printf(
                    "%sDuración errónea.%s%n%s", Utils.RED, Utils.RESET, message
                );
            }
    
        } while (!ok);
    
        return duration;
    }

    public static Duration readDuration(String from, String to, String message) {
        Duration duration;
        Duration durationFrom = toDuration(from);
        Duration durationTo = toDuration(to);
        if (durationFrom.compareTo(durationTo) > 0) {
            Duration tempFrom = durationFrom;
            durationFrom = durationTo;
            durationTo = tempFrom;
        }

        do {
            duration = readDuration(message);
            // Si el valor está fuera del rango
            if (duration.compareTo(durationFrom) < 0 || duration.compareTo(durationTo) > 0) {
                System.out.printf("%sValor fuera de rango.%s%n", Utils.RED, Utils.RESET);
                continue;
            } else break;
        } while (true);
        
        return duration;
    }

    // This does roughly the same as Utils.optionsString(),
    // except it builds options out of an enum rather than a String array
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T readEnum(Class<T> c, String message) {
        message = String.format("%s%s%s", Utils.BLUE, message, Utils.RESET);
        Object[] allItems = (EnumSet.allOf(c)).toArray();

        int i;
        for (i = 0; i < allItems.length; i++) {
            message += String.format("%n%3d - %s", i + 1, allItems[i]);
        }
        
        message = String.format(
            "%s%n%sElija una opción entre 1 y %d:%s ",
            message, Utils.BLUE, allItems.length, Utils.RESET
        );

        do {
            i = readInt(message);
            System.out.println();
        } while (i < 1 || i > allItems.length);

        return (T) allItems[i - 1];
    }
}

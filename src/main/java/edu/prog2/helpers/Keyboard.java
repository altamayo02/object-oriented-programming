package helpers;

import java.io.Console;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Keyboard {
    private static Console con = System.console();
    public static Scanner sc = new Scanner(con.reader())
       .useDelimiter("[\n]+|[\r\n]+");

    public static String readString(String message) {
        System.out.print(message);
        return sc.nextLine();
    }

    public static int readInt(String message) {
        boolean ok;
        int value = Integer.MIN_VALUE;
        System.out.print(message);
    
        do {
            try {
                ok = true;
                value = sc.nextInt();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.print(">> Valor erróneo. " + message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
    
        return value;
    }
}
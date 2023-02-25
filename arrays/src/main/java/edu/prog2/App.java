package edu.prog2;

import edu.prog2.helpers.*;
import edu.prog2.model.*;

public class App {
    static Matrix matrix = new Matrix(4, 5);

    public static void main(String[] args) {
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
                        asignarValores();
                        break;

                    case 2:
                        crearMatrizCuadrada();
                        break;

                    case 3:
                        pruebaReferencia();
                        break;

                    case 4:
                        pruebaCopia();
                        break;

                    case 5:
                        traspuesta();
                        break;

                    case 6:
                        matrizIdentidad();
                        break;

                    case 7:
                        sumarMatrices();
                        break;

                    case 8:
                        productoMatrices();
                        break;

                    case 9:
                        matrizTriangularSuperior();
                        break;

                    case 10:
                        matrizTriangularInferior();
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

    private static void asignarValores() {
        // TODO - Dig up the one code I made for matrix formatting
        int filas = Keyboard.readInt("Ingrese el número de filas: ");
        int columnas = Keyboard.readInt("Ingrese el número de columnas: ");
        matrix = new Matrix(filas, columnas);

        System.out.printf(
                "%nMatriz inicial de %d filas por %d columnas:%n%n",
                matrix.getRows(), matrix.getColumns());
        matrix.display();
        
        // Se asignan valores en dos celdas dentro del tamaño de la matriz al azar
        matrix.setValue(Utils.getRandom(0, filas), Utils.getRandom(0, columnas), 20);
        matrix.setValue(Utils.getRandom(0, filas), Utils.getRandom(0, columnas), 10);

        System.out.println("\nMatriz luego de asignarle valores:\n");
        matrix.display();
    }

    private static void crearMatrizCuadrada() {
        int tamanio = Keyboard.readInt("Tamaño de la matriz cuadrada: ");
        Matrix m2 = new Matrix(tamanio);

        int inicio = Keyboard.readInt("Valor mínimo para llenar celdas: ");
        int fin = Keyboard.readInt("Valor máximo para llenar celdas: ");
        m2.randomize(inicio, fin);

        System.out.println();
        m2.display();
    }

    private static void pruebaReferencia() {
        System.out.println();
        matrix.randomize(10, 100);
        matrix.display();
        System.out.println();

        int[][] m = matrix.getData();
        for (int j = 0; j < m[1].length; j++) {
            m[1][j] = m[1][j] + 100;
        }
        matrix.display();
    }

    /**
     * A diferencia del método {@code pruebaReferencia}, aquí la variable {@code m} es una nueva matriz que
     * contiene los mismos datos que la variable {@code matrix}. En {@code pruebaReferencia}, la variable
     * {@code m} está siendo destinada a referenciar la misma dirección de memoria que la variable
     * {@code matrix} referencia, o sea, allí {@code m} y {@code matrix} son la misma matriz.
     */
    private static void pruebaCopia() {
        System.out.println();
        matrix.randomize(10, 100);
        matrix.display();
        System.out.println();

        int[][] m = matrix.copyData();
        for (int j = 0; j < m[1].length; j++) {
            m[1][j] = m[1][j] + 100;
        }
        matrix.display();
    }

    private static void traspuesta() {
        System.out.println();
        matrix.randomize(100, 999);
        matrix.display();
        //Matrix t = new Matrix(matrix.transposedMatrix());
        Matrix t = matrix.transposedMatrix();
        System.out.printf(
          "\nLas dimensiones de la matriz transpuesta son (%d, %d):\n\n",
          t.getRows(), t.getColumns()
        );
        t.display();
    }

    public static void matrizIdentidad() {
        Matrix id = Matrix.identity(Keyboard.readInt("Ingrese el tamaño de la matriz identidad: "));
        
        System.out.println();
        id.display();
    }

    public static void sumarMatrices() {
        matrix.randomize(0, 9);
        System.out.println("Matriz A:\n");
        matrix.display();

        Matrix m2 = new Matrix(matrix.getRows(), matrix.getColumns(), 0, 9);
        Matrix r = matrix.sum(m2);

        System.out.println("Matriz B:\n");
        m2.display();
        System.out.println("\nMatriz (A + B):\n");
        r.display();
    }

    public static void productoMatrices() {
        matrix.randomize(0, 9);
        System.out.println("Matriz A:\n");
        matrix.display();

        int filas = Keyboard.readInt("\nIngrese el número de filas de la matriz B: ");
        int columnas = Keyboard.readInt("Ingrese el número de columnas de la matriz B: ");
        Matrix m2 = new Matrix(filas, columnas, 0, 9);
        Matrix r =  matrix.product(m2);
        
        System.out.println("Matriz B:\n");
        m2.display();
        System.out.println("\nMatriz (A × B):\n");
        r.display();
    }

    private static void matrizTriangularSuperior() {
        int tamanio = Keyboard.readInt("Ingrese el tamaño de la matriz: ");
        Matrix.upperTriangular(tamanio).display();
    }

    private static void matrizTriangularInferior() {
        int tamanio = Keyboard.readInt("Ingrese el tamaño de la matriz: ");
        Matrix.lowerTriangular(tamanio).display();
    }
}

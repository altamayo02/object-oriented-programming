package edu.prog2.model;

// ¿Debería el paquete helpers ser importado en vez de estos individualmente?
// Evitaría repetir código, pero la mayoría del contenido de las clases no sería usado.
import java.util.Random;
import java.util.InputMismatchException;

public class Matrix {
    private int[][] data;
    private int rows;
    private int columns;

    // TODO - Lambda function / Reference methods (?) for iterating through matrices

    public Matrix(int rows, int columns) {
        if (rows < 2 || columns < 2) {
            throw new InputMismatchException(String.format(
              "Las dimensiones %d×%d no componen una matriz bidimensional.", rows, columns
            ));
        }

        this.rows = rows;
        this.columns = columns;
        data = new int[rows][columns];
    }

    public Matrix(int size) {
        this(size, size);
    }

    public Matrix(int[][] m) {
        this(m.length, m[0].length);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = m[i][j];
            }
        }
    }

    public Matrix(Matrix m) {
        this(m.getData());
    }

    public Matrix(int rows, int cols, int min, int max) {
        this(rows, cols);
        randomize(min, max);
    }

    /**
     * Retorna una referencia a los datos de la matriz.
     * @return La referencia al atributo {@code data} de la instancia.
     */
    public int[][] getData() {
        return data;
    }

    public int getValue(int i, int j) {
        return data[i][j];
    }

    public void setValue(int i, int j, int value) {
        data[i][j] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    /**
     * Retorna una copia de los datos de la matriz.
     * @return Los valores del atributo {@code data} de la instancia.
     */
    public int[][] copyData() {
        int[][] copy = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                copy[i][j] = data[i][j];
            }
        }
        return copy;
    }
    
    public void display() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.printf("%5d", data[i][j]);
            }
            System.out.println();
        }
    }

    // Pobla la matriz con valores dentro del rango especificado
    public void randomize(int min, int max) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // max + 1, para que el valor máximo sea inclusivo
                setValue(i, j, getRandom(min, max + 1));
            }
        }
    }

    public Matrix sum(Matrix m) {
        if (m == null) {
            System.out.println();
            throw new NullPointerException("Se esperaba una matriz.");
        }
        if (rows != m.getRows() || columns != m.getColumns()) {
            System.out.println();
            throw new ArrayIndexOutOfBoundsException("Las matrices no son de igual tamaño");
        }
        
        Matrix sum = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sum.setValue(i, j, data[i][j] + m.getValue(i, j));
            }
        }
        return sum;
    }

    public Matrix product(Matrix m) {
        if (m == null) {
            System.out.println();
            throw new NullPointerException("Se esperaba una matriz.");
        }
        if (columns != m.getRows()) {
            System.out.println();
            throw new ArrayIndexOutOfBoundsException(
                "El número de columnas de la matriz A no es igual al número de filas de la matriz B"
            );
        }

        Matrix product = new Matrix(rows, m.getColumns());
        for (int i = 0; i < product.getRows(); i++) {
            for (int j = 0; j < product.getColumns(); j++) {
                int sum = 0;
                for (int cell = 0; cell < columns; cell++) {
                    sum += data[i][cell] * m.getValue(cell, j);
                }
                product.setValue(i, j, sum);
            }
        }
        return product;
    }

    // En el instructivo este método retorna un tipo int[][],
    // pero puede también retornar una instancia Matrix
    public Matrix transposedMatrix() {
        //int[][] transposed = new int[columns][rows];
        Matrix transposed = new Matrix(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                //transposed[j][i] = data[i][j];
                transposed.setValue(j, i, data[i][j]);
            }
        }
        return transposed;
    }

    public static Matrix identity(int size) {
        Matrix identity = new Matrix(size);
        for (int i = 0; i < size; i++) {
            identity.setValue(i, i, 1);
        }
        return identity;
    }

    public static Matrix upperTriangular(int size) {
        Matrix upperTriangular = new Matrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i <= j) upperTriangular.setValue(i, j, getRandom(1, 101));
            }
        }
        return upperTriangular;
    }

    public static Matrix lowerTriangular(int size) {
        Matrix lowerTriangular = new Matrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i >= j) lowerTriangular.setValue(i, j, getRandom(1, 101));
            }
        }
        return lowerTriangular;
    }

    // Este método no cumple una función relacionada con la clase Matrix
    public static int getRandom(int min, int max) {
        if (max < min) {
            int temp = min;
            min = max;
            max = temp;
        }

        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}

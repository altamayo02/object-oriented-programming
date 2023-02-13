package model;

public class Matrix {
    private int[][] data;
    private int rows;
    private int cols;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.cols = columns;
        data = new int[rows][columns]
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setValue(int i, int j, int val) {
        data[i][j] = val;
    }
}
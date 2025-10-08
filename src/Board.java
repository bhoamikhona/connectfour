/**
 * Board holds the 2D grid state and board-level operations.
 * It provides drop/undo, win/draw checks, and ASCII printing.
 */
public class Board {
    private int rows;
    private int cols;
    private int connect; // number in a row needed to win
    private char[][] grid;

    /**
     * @param rows
     * @param cols
     * @param connect
     */
    public Board(int rows, int cols, int connect) {
        this.rows = rows;
        this.cols = cols;
        this.connect = connect;
        this.grid = new char[rows][cols];
        clear();
    }

    /**
     * Looping through each row and each column, and setting its
     * value to an empty space character. Thereby clearing the
     * grid.
     */
    public void clear() {
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols - 1; c++) {
                grid[r][c] = ' ';
            }
        }
    }

    /**
     * Accessor method for rows
     * @return rows
     */
    public int getRows() { return rows; }

    /**
     * Accessor method for cols
     * @return cols
     */
    public int getCols() { return cols; }

    /**
     * Accessor method for connect
     * @return connect
     */
    public int getConnect() { return connect; }

    /**
     * If the 0th row and nth column is not an empty space then return true, otherwise return false
     * @return true if the specified column is full (i.e., top cell is not empty).
     */
    public boolean isColumnFull(int col) {
        return grid[0][col] != ' ';
    }

    /**
     * Drop a token into a column (falls to the lowest empty cell).
     * @return row index where the token landed, or -1 if the column is full.
     */
    public int drop(int col, char token) {
        // Throwing an error if the number of column entered is invalid
        if (col < 0 || col >= cols) throw new IllegalArgumentException("Invalid column");

        // Checking if the column is full
        if (isColumnFull(col)) return -1;

        // Looping through each row for the specific column from bottom up
        // The first empty slot will be filled with the token
        // The index of the row will be returned
        for (int r = rows - 1; r >= 0; r--) {
            if (grid[r][col] == ' ') {
                grid[r][col] = token;
                return r;
            }
        }
        return -1; // column full
    }

    /** TODO: Undo a move at (row, col) by clearing the cell. */
    public void undo(int row, int col) {}

    /** TODO: You will need to implement this function. @return true if the board has no empty cells left. */
    public boolean isFull() {
        return true;
    }

    /**
     * Check if the last move at (row, col) created a connect-N in any direction.
     * It counts in four direction pairs and subtracts 1 to avoid double counting the origin.
     * TODO: check if the last move is a winning move
     */
    public boolean isWinningMove(int row, int col) {
        return true;
    }


    // TODO: recursion to count in a direction
    private int countDirection(int r, int c, int dr, int dc, char token) {
        return 0;
    }

    /** TODO: Print the board in ASCII with row/column headers. */
    public void print() {}
}

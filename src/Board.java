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
     * TODO: Initialize the board with the specified dimensions and connect-N.
     * @param rows
     * @param cols
     * @param connect
     */
    public Board(int rows, int cols, int connect) {
        this.rows = 0;
        this.cols = 0;
        this.connect = 0;
        this.grid = new char[0][0];
        clear();
    }

    /** TODO: Clear the board to all spaces. */
    public void clear() {}

    /** TODO:  You will need to fix the return values of these methods */
    public int getRows() { return 0; }
    public int getCols() { return 0; }
    public int getConnect() { return 0; }

    /**
     * TODO: You need to implement the logic to see if a column is full
     * @return true if the specified column is full (i.e., top cell is not empty).
     */
    public boolean isColumnFull(int col) {
        return true;
    }

    /**
     * Drop a token into a column (falls to the lowest empty cell).
     * @return row index where the token landed, or -1 if the column is full.
     * 
     * TODO: finish the for loop
     */
    public int drop(int col, char token) {
        for (int r = rows - 1; r >= 0; r--) {

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

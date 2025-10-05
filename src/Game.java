import java.util.Scanner;

/**
 * The Game class orchestrates the console loop: reading commands,
 * dropping pieces, checking for wins, and handling undo/restart.
 *
 */
public class Game {

    private static int ROWS = 6;
    private static int COLS = 7;
    private static int CONNECT = 4;

    private  Board board;
    private  MoveStack undoStack;
    private  TurnQueue turnQueue;
    private  Scanner scanner;

    public Game() {
        /**
         * TODO: Initialize the board, undoStack, turnQueue with the correct parameters/values
         */
        this.board = new Board(0, 0, 0);
        this.undoStack = new MoveStack(0);
        this.turnQueue = new TurnQueue(0); // capacity for up to 4 players
        this.scanner = new Scanner(System.in);

        // Two players by default
        turnQueue.enqueue(new Player("Player 1", 'X'));
        turnQueue.enqueue(new Player("Player 2", 'O'));
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  [0-" + (board.getCols() - 1) + "]  -> drop a piece in that column");
        System.out.println("  undo        -> undo the last move");
        System.out.println("  board       -> reprint the current board");
        System.out.println("  restart     -> clear the board and start over");
        System.out.println("  help        -> show this help menu");
        System.out.println("  quit        -> exit the game");
        System.out.println();
    }

    public void run() {
        System.out.println("=== Connect-Four Mini (Terminal) ===");
        System.out.println("Goal: connect " + board.getConnect() + " in a row.");
        System.out.println();
        printHelp();
        board.print();

        boolean keepPlaying = true;
        while (keepPlaying) {
            /** TODO:
             * Uncomment these lines when the functions have been implemented 
             * */
            // Player current = turnQueue.peek();
            // System.out.print(current.name() + " (" + current.token() + "), enter command: ");

            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("quit")) {
                keepPlaying = false;
                System.out.println("Goodbye!");
            } else if (line.equalsIgnoreCase("help")) {
                printHelp();
            } else if (line.equalsIgnoreCase("board")) {
                board.print();
            } else if (line.equalsIgnoreCase("restart")) {
                restart();
            } else if (line.equalsIgnoreCase("undo")) {
                handleUndo();
            } else {

                /**
                 * TODO: Try to parse the input as a column number and drop a piece there.
                 * Also handle implement error handling
                 */
                try {
                    int col = Integer.parseInt(line);
                  
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid command. Type a column number, or try 'help'.");
                }
            }
        }
    }

    /**
     * TODO: Handle dropping a piece in the specified column.
     * @param col
     */
    private void handleDrop(int col) {}

    /**
     * TODO: Handle undoing the last move
     */
    private void handleUndo() {}

    /**
     * TODO: Restart the game
    */

    private void restart() {}
}

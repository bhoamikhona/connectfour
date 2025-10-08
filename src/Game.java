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
         * TODO: Initialize the board, undoStack, turnQueue with the correct parameters/values - COMPLETE
         */
        this.board = new Board(ROWS, COLS, CONNECT);
        this.undoStack = new MoveStack(ROWS * COLS);
        this.turnQueue = new TurnQueue(4); // capacity for up to 4 players
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
             * Uncomment these lines when the functions have been implemented - COMPLETE
             * */
            // Ask the TurnQueue who is going to play next (front of the queue)
             Player current = turnQueue.peek();

             // Prompt the current player for a command (column number or a keyword)
             System.out.print(current.name() + " (" + current.token() + "), enter command: ");

            // If the input stream is closed, exit the loop
            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine().trim();


            // If the player typed "quit", stop the loop and exit
            if (line.equalsIgnoreCase("quit")) {
                keepPlaying = false;
                System.out.println("Goodbye!");
            // If the player typed "help, print the help menu
            } else if (line.equalsIgnoreCase("help")) {
                printHelp();
            // If the player typed "board", print the current board state
            } else if (line.equalsIgnoreCase("board")) {
                board.print();
            // If the player typed "restart", reset the game state
            } else if (line.equalsIgnoreCase("restart")) {
                restart();
            // If the player typed "undo", attempt to undo the last move
            } else if (line.equalsIgnoreCase("undo")) {
                handleUndo();
            } else {

                /**
                 * TODO: Try to parse the input as a column number and drop a piece there. - COMPLETE
                 * Also handle implement error handling
                 */
                try {
                    // store the number in col variable
                    int col = Integer.parseInt(line);
                    // attempt to apply the command and show the updated board
                    handleDrop(col);
                    board.print();
                  
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid command. Type a column number, or try 'help'.");
                } catch (IllegalArgumentException iae) {
                    System.out.println(iae.getMessage());
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

    private void restart() {
        board.clear(); //this will clear the board
        undoStack.clear(); //This will clear the undo stack
        turnQueue.clear(); // this will reset the turn queue

        //now we should add the two players in order again
        turnQueue.enqueue(new Player("Player 1",'X'));
        turnQueue.enqueue(new Player("Player 2" , 'O'));

        System.out.println("Game restarted!"); //we print a confirmation message
        board.print(); //print the empty board again


    }
}

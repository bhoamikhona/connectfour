import java.util.Scanner;

/**
 * The Game class orchestrates the console loop: reading commands,
 * dropping pieces, checking for wins, and handling undo/restart.
 */
public class Game {

    private static int ROWS = 6;
    private static int COLS = 7;
    private static int CONNECT = 4;

    private Board board;
    private MoveStack undoStack;
    private TurnQueue turnQueue;
    private Scanner scanner;

    private boolean gameOver; // set to true when win/draw/restart

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
        System.out.println("  undo        -> undo the last move (disabled after game ends)");
        System.out.println("  board       -> reprint the current board");
        System.out.println("  restart     -> clear the board and start over");
        System.out.println("  help        -> show this help menu");
        System.out.println("  hint        -> Show safe and recommended moves");
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
            if (gameOver) {
                System.out.print("Game over. Type 'restart' to play again, 'quit' to exit, or 'help' for options: ");

                // Post-game: accept ONLY restart/quit/help/board. Undo is disallowed.
                if (!scanner.hasNextLine()) {
                    keepPlaying = false;
                    System.out.println("Goodbye!");
                } else {
                    String line = scanner.nextLine().trim();

                    if (line.equalsIgnoreCase("restart")) {
                        restart(); // sets gameOver=false
                    } else if (line.equalsIgnoreCase("quit")) {
                        keepPlaying = false;
                        System.out.println("Goodbye!");
                    } else if (line.equalsIgnoreCase("help")) {
                        printHelp();
                    } else if (line.equalsIgnoreCase("board")) {
                        board.print();
                    } else if (line.equalsIgnoreCase("undo")) {
                        System.out.println("Undo is not allowed after the game ends. Type 'restart' to play again.");
                    } else {
                        System.out.println("After the game ends, only 'restart', 'board', 'help', or 'quit' are allowed.");
                    }
                }

            } else {
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
                    // If the player typed "undo", attempt to undo the last move (allowed only mid-game)
                } else if (line.equalsIgnoreCase("undo")) {
                    handleUndo();
                } else if (line.equalsIgnoreCase("hint")) {
                    handleHint();
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
    }

    /**
     * TODO: Handle dropping a piece in the specified column. Completed
     *
     * @param col
     */
    private void handleDrop(int col) {

        //it will check if the picked column is valid
        if (col < 0 || col >= board.getCols()) {
            throw new IllegalArgumentException("Unacceptable column number! Please try again.");
        }

        Player current = turnQueue.peek(); //it will get current player

        int row = board.drop(col, current.token()); //it tells us the token that is passed is on which row

        //if the column is full gives us message to change it to another column
        if (row == -1) {
            System.out.println("That column is full. Try another one.");
            return;
        }

        //it will save the current move so it can be undone later
        Move move = new Move(row, col, current.token());
        undoStack.push(move);

        //in this step we will check if the current player won
        if (board.isWinningMove(row, col)) {
            System.out.println("Congratulations!" + current.name() + "(" + current.token() + ") wins!");
            System.out.println("Type 'restart' to play again or 'quit' to exit.");
            gameOver = true;      // lock further numeric input
            return;               // do not rotate after win
        }

        //it will check for a draw
        if (board.isFull()) {
            System.out.println("It is a draw! Type 'restart' to play again or 'quit' to exit.");
            gameOver = true;      // lock further numeric input
            return;
        }

        turnQueue.rotate();   //it will move to the next player

       // handleHint(); // show hint for the next player automatically

    }

    /**
     * TODO: Handle undoing the last move  - Completed
     */
    private void handleUndo() {
        // Disallow undo after game end
        if (gameOver) {
            System.out.println("Undo is not allowed after the game ends. Type 'restart' to play again.");
            return;
        }

        if (undoStack.isEmpty()) {
            System.out.println("There is no move to undo.");
            return;
        }

        // will get the last move from the stack
        Move lastMove = undoStack.pop();

        // will undo that move on the board
        board.undo(lastMove.row(), lastMove.col());

        // now  it will rotate turn back to previous player
        turnQueue.rotate();

        // will print a confirmation message
        System.out.println("Last move undone. Back to " + turnQueue.peek().name() + ".");
        board.print();
    }

    /**
     * TODO: Restart the game - Completed
     */
    private void restart() {
        board.clear(); //this will clear the board
        undoStack.clear(); //This will clear the undo stack
        turnQueue.clear(); // this will reset the turn queue

        //now we should add the two players in order again
        turnQueue.enqueue(new Player("Player 1", 'X'));
        turnQueue.enqueue(new Player("Player 2", 'O'));

        System.out.println("Game restarted!"); //we print a confirmation message
        board.print(); //print the empty board again
        gameOver = false;
    }

    /** Here we define a method to handle the hints
     * TODO: Handle showing a hint for the current player - Completed
     * **/

    /**
     * Handle showing a hint for the current player
     */
    private void handleHint() {
        Player current = turnQueue.peek();       // current player
        Player opponent = turnQueue.peekNext();  // next player, if any

        char oppToken = (opponent != null)
                ? opponent.token()
                : (current.token() == 'X' ? 'O' : 'X');  // fallback if only 1 player

        Hints hints = board.getHints(current.token(), oppToken);

        System.out.println();
        System.out.println("=== HINT for " + current.name() + " (" + current.token() + ") ===");
        hints.print();
        System.out.println();
    }



}

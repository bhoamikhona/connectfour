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

    /**
     * AI Fields
     */
    private AIPlayer aiPlayer = null;  //AI player object
    private boolean vsAI = false; //Are we playing vs AI?
    private String aiLevel = ""; //random, med, or hard
    private char humanToken = 'X'; //human is X, AI will be O

    private PlayerStore playerStore = new PlayerStore("playerdata.txt");



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
        System.out.println();
        System.out.println("  game ai random -> start Human vs AI (Easy Level)");
        System.out.println("  game ai med -> start Human vs AI (Medium Level)");
        System.out.println("  game ai hard   -> start Human vs AI (Hard level)");
        System.out.println();
        System.out.println("register <name> -> register a new player profile");
        System.out.println("create tournament <id> <player1> <player2> ... -> create a tournament with the given players");
        System.out.println("start tournament <id> -> start playing the tournament with the given id");
        System.out.println("tournament standings <id> -> show the current standings of the tournament with the given id");
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

                // If we are NOT in AI mode, use the traditional TurnQueue
                if (!vsAI) {
                    Player current = turnQueue.peek();
                    System.out.print(current.name() + " (" + current.token() + "), enter command: ");
                }
                //  If we ARE in AI mode, only ask the HUMAN for input
                else {
                    System.out.print("Human (X), enter command: ");
                }

//                // Ask the TurnQueue who is going to play next (front of the queue)
//                Player current = turnQueue.peek();
//
//                // Prompt the current player for a command (column number or a keyword)
//                System.out.print(current.name() + " (" + current.token() + "), enter command: ");

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
                    /**
                     * Start a gamme vs AI
                     */
                } else if  ( line.toLowerCase().startsWith("game ai")){
                    String[] parts =line.split("\\s+");

                    if (parts.length < 3) {
                        System.out.println("Usage: game ai < random | med | hard >");
                    } else {
                        String level = parts[2].toLowerCase();

                        if (level.equals ("random") || level.equals("med") || level.equals("hard")) {

                            //Enable AI mode
                            vsAI = true;
                            aiLevel = level;
                            humanToken = 'X';
                            aiPlayer = new AIPlayer('O');
                            gameOver = false;

                            //Reset the game state
                            board.clear();
                            undoStack.clear();
                            turnQueue.clear(); //this is not used in AI mode

                            System.out.println("Starting Human vs AI game");
                            System.out.println("AI difficulty:" + aiLevel);
                            System.out.println("You are X and AI is O");
                            board.print();

                        } else{
                            System.out.println("Unknown difficulty. Use: random, med, or hard");
                        }
                    } 



                } 
                else if (line.toLowerCase().startsWith("register")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("Usage: register <player_name>");
                    } else {
                        String playerName = parts[1];
                        String mess = playerStore.register( playerName);
                        System.out.println(mess);
                    }
                }
                else if (line.toLowerCase().startsWith("create tournament")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length < 6){
                        System.out.println("Tournament needs at least 3 players to start.");
                    }
                    else {
                        try{
                            handleTournamentCreation(parts);
                        }catch (IllegalArgumentException iae){
                            System.out.println(iae.getMessage());
                        }
                    }
                }

                else if (line.toLowerCase().startsWith("start tournament")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 3) {
                        System.out.println("Usage: start tournament <tournament_id>");
                    } else {
                        int tournamentId;
                        try {
                            tournamentId = Integer.parseInt(parts[2]);
                            Tournament tournament = TournamentManager.getTournament(tournamentId);
                            if (tournament == null) {
                                System.out.println("Tournament with ID " + tournamentId + " does not exist.");
                            } else {
                                System.out.println("Starting Tournament ID: " + tournamentId);
                                while (tournament.hasMoreMatches()) {
                                    Match match = tournament.playNextMatch();
                                    System.out.println("Match: " + match.getPlayer1().getName() + " vs " + match.getPlayer2().getName());
                                    playMatchWithAI(match, tournament);
                                }
                                System.out.println("Tournament ID: " + tournamentId + " has concluded. Final Standings:");
                                tournament.printStandings();
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid tournament ID. It must be an integer.");
                        }
                    }
                }

                else if(line.toLowerCase().startsWith("tournament standings")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 3) {
                        System.out.println("Usage: tournament standings <tournament_id>");
                    } else {
                        int tournamentId;
                        try {
                            tournamentId = Integer.parseInt(parts[2]);
                            Tournament tournament = TournamentManager.getTournament(tournamentId);
                            if (tournament == null) {
                                System.out.println("Tournament with ID " + tournamentId + " does not exist.");
                            } else {
                                System.out.println("Current Standings for Tournament ID: " + tournamentId);
                                tournament.printStandings();
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid tournament ID. It must be an integer.");
                        }
                    }
                }
                else {

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

                        // AI MOVE (only if in AI mode, and game is NOT over)
                        if (vsAI && !gameOver) {

                            // AI decides a column
                            int aiColumn;

                            if (aiLevel.equals("random")) {
                                aiColumn = aiPlayer.randomMove(board);
                            } else if (aiLevel.equals("med")) {
                                aiColumn = aiPlayer.mediumMove(board);
                            } else {
                                aiColumn = aiPlayer.hardMove(board);
                            }

                            // Place AI’s move on the board
                            int aiRow = board.drop(aiColumn, aiPlayer.getToken());
                            undoStack.push(new Move(aiRow, aiColumn, aiPlayer.getToken()));

                            System.out.println("AI moves at column " + aiColumn);
                            board.print();

                            // Check win or draw for AI
                            if (board.isWinningMove(aiRow, aiColumn)) {
                                System.out.println("AI wins!");
                                gameOver = true;
                            } else if (board.isFull()) {
                                System.out.println("Game is a draw!");
                                gameOver = true;
                            }
                        }



                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid command. Type a column number, or try 'help'.");
                    } catch (IllegalArgumentException iae) {
                        System.out.println(iae.getMessage());
                    }
                }
            }
        }
    }

    private void handleTournamentCreation(String[] parts) {
        //parts[0] = create
        //parts[1] = tournament
        //parts[2] = <tournament_id>
        //parts[3...] = player names

        int tournamentId;
        try {
            tournamentId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid tournament ID. It must be an integer.");
        }

        if (parts.length < 6) {
            throw new IllegalArgumentException("Tournament needs at least 3 players to start.");
        }
        PlayerProfile[] tournamentPlayers = new PlayerProfile[parts.length - 3];
        for (int i = 3; i < parts.length;i++){
            String playerName = parts[i];
            PlayerProfile p = playerStore.getProfileObject(playerName); 
            if (p == null){
                throw new IllegalArgumentException("Player '" + playerName + "' does not exist in the player store.");
            }else{
                System.out.println("Added player '" + playerName + "' to tournament " + tournamentId);
                tournamentPlayers[i - 3] = p;
            }
        }
        Tournament tournament = new Tournament(tournamentId, tournamentPlayers);
        TournamentManager.addTournament(tournament);

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

        char currentToken;
        String currentName;

        if (vsAI) {
            currentToken = humanToken;   // X
            currentName = "Human";
        } else {
            Player current = turnQueue.peek();
            currentToken = current.token();
            currentName = current.name();
        }


        int row = board.drop(col, currentToken); //it tells us the token that is passed is on which row

        //if the column is full gives us message to change it to another column
        if (row == -1) {
            System.out.println("That column is full. Try another one.");
            return;
        }

        //it will save the current move so it can be undone later
        Move move = new Move(row, col, currentToken);
        undoStack.push(move);

        //in this step we will check if the current player won
        if (board.isWinningMove(row, col)) {
            System.out.println("Congratulations!" + currentName + "(" + currentToken + ") wins!");
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
        if (!vsAI) {
            turnQueue.rotate();   //only rotate players in human vs human mode

            if (vsAI && !gameOver) {
                int aiCol;

                if (aiLevel.equals("random")) {
                    aiCol = aiPlayer.randomMove(board);
                } else if (aiLevel.equals("med")) {
                    aiCol = aiPlayer.mediumMove(board);
                } else {
                    aiCol = aiPlayer.hardMove(board);
                }

                int aiRow = board.drop(aiCol, aiPlayer.getToken());

                Move aiMove = new Move(aiRow, aiCol, aiPlayer.getToken());
                undoStack.push(aiMove);

                System.out.println("AI played column " + aiCol);
                board.print();

                if (board.isWinningMove(aiRow, aiCol)) {
                    System.out.println("AI wins! Type 'restart' or 'quit'.");
                    gameOver = true;
                } else if (board.isFull()) {
                    System.out.println("It is a draw! Type 'restart' or 'quit'.");
                    gameOver = true;
                }
            }

        }

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

        // now  it will rotate turn back to previous player (If it is vs human)
        if (!vsAI) {
            turnQueue.rotate();
        }
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

        char currentToken;
        char opponentToken;
        String playerLabel;

        // HUMAN vs AI MODE
        if (vsAI) {

            currentToken = humanToken;          // Human is X
            opponentToken = aiPlayer.getToken(); // AI is O
            playerLabel = "Human";

        }
        //  HUMAN vs HUMAN MODE
        else {

            Player current = turnQueue.peek();
            Player opponent = turnQueue.peekNext();

            currentToken = current.token();
            playerLabel = current.name();

            if (opponent != null) {
                opponentToken = opponent.token();
            } else {
                if (currentToken == 'X') {
                    opponentToken = 'O';
                } else {
                    opponentToken = 'X';
                }
            }
        }

        // Generate and print hints
        Hints hints = Hints.getHints(currentToken, opponentToken, board);

        System.out.println();
        System.out.println("=== HINT for " + playerLabel + " (" + currentToken + ") ===");
        hints.print();
        System.out.println();
    }

    private void playMatchWithAI(Match match, Tournament tournament) {

        // Create a fresh board for the match
        Board matchBoard = new Board(ROWS, COLS, CONNECT);
        matchBoard.clear();

        // Create two AI players
        AIPlayer aiPlayer1 = new AIPlayer('X');
        AIPlayer aiPlayer2 = new AIPlayer('O');

        PlayerProfile player1 = match.getPlayer1();
        PlayerProfile player2 = match.getPlayer2();

        boolean player1Turn = true;

        System.out.println("----------------------------------");
        System.out.println("Starting match: "
                + player1.getName() + " (X) vs "
                + player2.getName() + " (O)");
        matchBoard.print();

        while (true) {

            AIPlayer currentAI;
            PlayerProfile currentPlayer;

            if (player1Turn) {
                currentAI = aiPlayer1;
                currentPlayer = player1;
            } else {
                currentAI = aiPlayer2;
                currentPlayer = player2;
            }

            // Choose move 
            int col = currentAI.hardMove(matchBoard);

            int row = matchBoard.drop(col, currentAI.getToken());

            System.out.println(
                    currentPlayer.getName()
                            + " (" + currentAI.getToken() + ") plays column " + col
            );

            matchBoard.print();

            // Check for win
            if (matchBoard.isWinningMove(row, col)) {
                System.out.println(currentPlayer.getName() + " wins the match!");

                PlayerProfile loser = player1Turn ? player2 : player1;

                currentPlayer.recordOverallResult(1);
                loser.recordOverallResult(-1);

                tournament.addWin(currentPlayer);
                tournament.addLoss(loser);

                waitForNextMatch();
                return;
            }


            // Check for draw
            if (matchBoard.isFull()) {
                System.out.println("Match ended in a draw.");

                player1.recordOverallResult(0);
                player2.recordOverallResult(0);
                waitForNextMatch();
                return;
            }

            // Switch turns
            player1Turn = !player1Turn;
        }

        
    }

    private void waitForNextMatch() {
        System.out.print("Type 'next' to continue to the next match: ");

        while (true) {
            if (!scanner.hasNextLine()) {
                return;
            }

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("next")) {
                System.out.println();
                return;
            } else {
                System.out.print("Invalid input. Please type 'next': ");
            }
        }
    }

}

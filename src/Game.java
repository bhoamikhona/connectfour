import java.util.Scanner;

/**
 * Game.java
 * ----------
 * This class is the "main controller" of the program.
 * It runs the console loop, reads commands, updates the board,
 * checks win/draw, handles undo/restart, supports AI mode,
 * supports player accounts, and supports tournament mode.
 */
public class Game {

    // Default Connect-Four settings (6 rows, 7 columns, connect 4 to win)
    private static int ROWS = 6;
    private static int COLS = 7;
    private static int CONNECT = 4;

    // Core game components
    private Board board;            // The board grid + win/draw logic
    private MoveStack undoStack;    // Stack to store moves for undo
    private TurnQueue turnQueue;    // Queue to rotate turns between players
    private Scanner scanner;        // Reads input from the terminal

    // When true, no more moves are allowed until restart (except board/help/quit)
    private boolean gameOver;

    // AI mode fields
    private AIPlayer aiPlayer = null; // AI player object (token will be 'O')
    private boolean vsAI = false;     // True if Human vs AI mode is active
    private String aiLevel = "";      // "random", "med", or "hard"
    private char humanToken = 'X';    // Human plays X by default

    // Player account storage (saved to a file)
    private PlayerStore playerStore = new PlayerStore("playerdata.txt");

    // Tournament mode fields
    private Tournament activeTournament = null; // The currently active tournament
    private Match pendingMatch = null;          // Not used right now (kept for future use)
    private boolean inTournamentMode = false;   // True when user is inside tournament mode

    /**
     * Constructor
     * Initializes the board, undo stack, queue, and scanner.
     * Also sets up default Human vs Human players.
     */
    public Game() {
        this.board = new Board(ROWS, COLS, CONNECT);
        this.undoStack = new MoveStack(ROWS * COLS);
        this.turnQueue = new TurnQueue(4);
        this.scanner = new Scanner(System.in);

        // Default players for normal (Human vs Human) mode
        turnQueue.enqueue(new Player("Player 1", 'X'));
        turnQueue.enqueue(new Player("Player 2", 'O'));
    }

    /**
     * Prints a clean command menu for the user.
     * W controls spacing so the menu lines up nicely.
     */
    private void printHelp() {
        final int W = 36;

        System.out.println("Commands:\n");

        // Helper lambda to print command + description in aligned format
        java.util.function.BiConsumer<String, String> line =
                (cmd, desc) -> System.out.printf("  %-" + W + "s -> %s%n", cmd, desc);

        // Core gameplay commands
        line.accept("[0-6]", "drop a piece in that column");
        line.accept("move [0-6]", "drop a piece in that column");
        line.accept("undo", "undo the last move (disabled after game ends)");
        line.accept("board", "reprint the current board");
        line.accept("restart", "clear the board and start over");
        line.accept("help", "show this help menu");
        line.accept("hint", "show safe/unsafe columns + a recommendation");
        System.out.println();

        // AI gameplay commands
        line.accept("game ai random", "start Human vs AI (Easy)");
        line.accept("game ai med", "start Human vs AI (Medium)");
        line.accept("game ai hard", "start Human vs AI (Hard)");
        System.out.println();

        // Player profile/account commands
        line.accept("register <name>", "register a new player profile");
        line.accept("login <name>", "login as an existing player");
        line.accept("logout", "logout current user");
        line.accept("whoami", "show current logged-in user");
        line.accept("profile <name>", "show a player profile");
        line.accept("leaderboard top <n>", "show top-n leaderboard (overall wins)");
        System.out.println();

        // Tournament commands
        line.accept("create tournament <id> <p1> <p2> <p3> ...", "create a tournament with the given players");
        line.accept("start tournament <id>", "enter tournament mode (use 'next' to play each match)");
        line.accept("next", "in tournament mode: play the next scheduled match");
        line.accept("tournament standings <id>", "show standings for the given tournament");
        System.out.println();

        // Exit
        line.accept("quit", "exit the game");
        System.out.println();
    }

    /**
     * Main game loop.
     * Keeps reading commands until user types 'quit' or input ends.
     */
    public void run() {
        System.out.println("=== Connect-Four Mini (Terminal) ===");
        System.out.println("Goal: connect " + board.getConnect() + " in a row.");
        System.out.println();
        printHelp();
        board.print();

        boolean keepPlaying = true;
        while (keepPlaying) {

            // ----------------------------
            // 1) TOURNAMENT MODE PROMPT
            // ----------------------------
            if (inTournamentMode) {
                // Safety check: if tournament is missing, exit tournament mode
                if (activeTournament == null) {
                    inTournamentMode = false;
                } else {
                    // If no matches remain, show final standings and exit tournament mode
                    if (!activeTournament.hasMoreMatches()) {
                        System.out.println("Tournament concluded. Final Standings:");
                        activeTournament.printStandings();
                        activeTournament = null;
                        inTournamentMode = false;
                    } else {
                        // Tournament prompt
                        System.out.print("[TOURNAMENT] Type 'next' to play next match, 'tournament standings <id>', or 'quit': ");
                    }
                }
            }

            // ----------------------------
            // 2) NORMAL GAME PROMPT
            // ----------------------------
            if (!inTournamentMode) {
                if (gameOver) {
                    // After game ends, allow only restart/help/board/quit
                    System.out.print("Game over. Type 'restart' to play again, 'quit' to exit, or 'help' for options: ");
                } else {
                    // If playing human vs human, show current player's name/token
                    if (!vsAI) {
                        Player current = turnQueue.peek();
                        System.out.print(current.name() + " (" + current.token() + "), enter command: ");
                    } else {
                        // If playing vs AI, always prompt human (X)
                        System.out.print("Human (X), enter command: ");
                    }
                }
            }

            // If input stream ends (rare), stop safely
            if (!scanner.hasNextLine()) {
                keepPlaying = false;
                System.out.println("Goodbye!");
                break;
            }

            // Read and normalize user input
            String line = scanner.nextLine().trim();
            String lower = line.toLowerCase();

            // ----------------------------
            // 3) GLOBAL COMMANDS (ALWAYS WORK)
            // ----------------------------
            if (lower.equals("quit")) {
                keepPlaying = false;
                System.out.println("Goodbye!");
                continue;
            }

            if (lower.equals("help")) {
                printHelp();
                continue;
            }

            if (lower.equals("board")) {
                board.print();
                continue;
            }

            if (lower.equals("restart")) {
                restart();
                continue;
            }

            // ----------------------------
            // 4) TOURNAMENT MODE COMMANDS
            // ----------------------------
            if (inTournamentMode) {
                if (lower.startsWith("tournament standings")) {
                    handleTournamentStandings(lower);
                    continue;
                }
                if (lower.equals("next")) {
                    playNextTournamentMatch();
                    continue;
                }

                // If user types something invalid in tournament mode
                System.out.println("Tournament mode: valid commands are 'next', 'tournament standings <id>', 'quit'.");
                continue;
            }

            // ----------------------------
            // 5) IF GAME IS OVER, BLOCK MOST COMMANDS
            // ----------------------------
            if (gameOver) {
                if (lower.equals("undo")) {
                    System.out.println("Undo is not allowed after the game ends. Type 'restart' to play again.");
                } else if (!lower.equals("restart") && !lower.equals("help") && !lower.equals("board")) {
                    System.out.println("After the game ends, only 'restart', 'board', 'help', or 'quit' are allowed.");
                }
                continue;
            }

            // ----------------------------
            // 6) IN-GAME COMMANDS
            // ----------------------------
            if (lower.equals("undo")) {
                handleUndo();
                continue;
            }

            if (lower.equals("hint")) {
                handleHint();
                continue;
            }

            // ----------------------------
            // 7) ACCOUNT COMMANDS
            // ----------------------------
            if (lower.startsWith("register")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Usage: register <player_name>");
                } else {
                    System.out.println(playerStore.register(parts[1]));
                }
                continue;
            }

            if (lower.startsWith("login")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Usage: login <player_name>");
                } else {
                    System.out.println(playerStore.login(parts[1]));
                }
                continue;
            }

            if (lower.equals("logout")) {
                System.out.println(playerStore.logout());
                continue;
            }

            if (lower.equals("whoami")) {
                System.out.println(playerStore.whoami());
                continue;
            }

            if (lower.startsWith("profile")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Usage: profile <player_name>");
                } else {
                    System.out.println(playerStore.profile(parts[1]));
                }
                continue;
            }

            if (lower.startsWith("leaderboard top")) {
                String[] parts = lower.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Usage: leaderboard top <n>");
                } else {
                    try {
                        int n = Integer.parseInt(parts[2]);
                        System.out.println(playerStore.leaderboardTop(n));
                    } catch (NumberFormatException nfe) {
                        System.out.println("N must be an integer.");
                    }
                }
                continue;
            }

            // ----------------------------
            // 8) START AI GAME COMMAND
            // ----------------------------
            if (lower.startsWith("game ai")) {
                String[] parts = lower.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Usage: game ai <random|med|hard>");
                    continue;
                }

                String level = parts[2];
                if (!level.equals("random") && !level.equals("med") && !level.equals("hard")) {
                    System.out.println("Unknown difficulty. Use: random, med, or hard");
                    continue;
                }

                // Turn on AI mode and reset board/moves
                vsAI = true;
                aiLevel = level;
                humanToken = 'X';
                aiPlayer = new AIPlayer('O');
                gameOver = false;

                board.clear();
                undoStack.clear();
                turnQueue.clear();

                System.out.println("Starting Human vs AI game");
                System.out.println("AI difficulty: " + aiLevel);
                System.out.println("You are X and AI is O");
                board.print();
                continue;
            }

            // ----------------------------
            // 9) TOURNAMENT CREATE COMMAND
            // ----------------------------
            if (lower.startsWith("create tournament")) {
                String[] parts = line.split("\\s+");
                if (parts.length < 6) {
                    // create tournament <id> <p1> <p2> <p3> --> minimum 6 tokens
                    System.out.println("Tournament needs at least 3 players to start.");
                } else {
                    try {
                        handleTournamentCreation(parts);
                        System.out.println("Tournament created successfully.");
                    } catch (IllegalArgumentException iae) {
                        System.out.println(iae.getMessage());
                    }
                }
                continue;
            }

            // ----------------------------
            // 10) TOURNAMENT START COMMAND
            // ----------------------------
            if (lower.startsWith("start tournament")) {
                String[] parts = lower.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Usage: start tournament <tournament_id>");
                    continue;
                }
                try {
                    int tournamentId = Integer.parseInt(parts[2]);
                    Tournament tournament = TournamentManager.getTournament(tournamentId);
                    if (tournament == null) {
                        System.out.println("Tournament with ID " + tournamentId + " does not exist.");
                    } else {
                        activeTournament = tournament;
                        inTournamentMode = true;
                        System.out.println("Entered tournament mode for Tournament ID: " + tournamentId);
                        System.out.println("Type 'next' to play the first match.");
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid tournament ID. It must be an integer.");
                }
                continue;
            }

            // Tournament standings command outside tournament mode
            if (lower.startsWith("tournament standings")) {
                handleTournamentStandings(lower);
                continue;
            }

            // ----------------------------
            // 11) MOVE INPUT (COLUMN OR "move N")
            // ----------------------------
            Integer maybeCol = parseMoveColumn(lower);
            if (maybeCol == null) {
                System.out.println("Invalid command. Type a column number, 'move N', or try 'help'.");
                continue;
            }

            // Try dropping a piece; catch errors like invalid column
            try {
                handleDrop(maybeCol);
                board.print();

                // If playing vs AI and game not ended, let AI take its move now
                if (vsAI && !gameOver) {
                    aiTakeTurn();
                }

            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
            }
        }
    }

    /**
     * Converts user input into a column number.
     * Accepts:
     *   - "move N"
     *   - "N"
     * Returns null if it is not a valid integer format.
     */
    private Integer parseMoveColumn(String lower) {
        // Format: move N
        if (lower.startsWith("move ")) {
            String[] parts = lower.split("\\s+");
            if (parts.length != 2) return null;
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        // Format: N
        try {
            return Integer.parseInt(lower);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * Makes the AI choose a column and drop its piece.
     * Also checks for AI win/draw and records result for logged-in user.
     */
    private void aiTakeTurn() {
        int aiColumn;

        // Choose AI move based on difficulty
        if (aiLevel.equals("random")) {
            aiColumn = aiPlayer.randomMove(board);
        } else if (aiLevel.equals("med")) {
            aiColumn = aiPlayer.mediumMove(board);
        } else {
            aiColumn = aiPlayer.hardMove(board);
        }

        // Drop AI token on the board and record move in undo stack
        int aiRow = board.drop(aiColumn, aiPlayer.getToken());
        undoStack.push(new Move(aiRow, aiColumn, aiPlayer.getToken()));

        System.out.println("AI moves at column " + aiColumn);
        board.print();

        // Check if AI won
        if (board.isWinningMove(aiRow, aiColumn)) {
            System.out.println("AI wins!");
            gameOver = true;
            recordVsAIResultForLoggedInUser(-1); // human loses
        }
        // Check if draw
        else if (board.isFull()) {
            System.out.println("Game is a draw!");
            gameOver = true;
            recordVsAIResultForLoggedInUser(0); // draw
        }
    }

    /**
     * Saves the result of Human vs AI ONLY if a user is logged in.
     * resultForHuman meanings:
     *   1  = human win
     *   0  = draw
     *  -1  = human loss
     */
    private void recordVsAIResultForLoggedInUser(int resultForHuman) {
        String who = playerStore.whoami();
        // If no user is logged in, do nothing
        if (who == null || who.equals("(none)")) {
            return;
        }
        playerStore.recordHumanVsAI(who, resultForHuman);
    }

    /**
     * Prints tournament standings for a given tournament ID.
     * Usage: tournament standings <id>
     */
    private void handleTournamentStandings(String lower) {
        String[] parts = lower.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: tournament standings <tournament_id>");
            return;
        }

        try {
            int tournamentId = Integer.parseInt(parts[2]);
            Tournament t = TournamentManager.getTournament(tournamentId);
            if (t == null) {
                System.out.println("Tournament with ID " + tournamentId + " does not exist.");
            } else {
                System.out.println("Current Standings for Tournament ID: " + tournamentId);
                t.printStandings();
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid tournament ID. It must be an integer.");
        }
    }

    /**
     * Plays the next match in the active tournament.
     * If no matches remain, it prints final standings and exits tournament mode.
     */
    private void playNextTournamentMatch() {
        // If tournament disappeared, exit tournament mode safely
        if (activeTournament == null) {
            System.out.println("No active tournament.");
            inTournamentMode = false;
            return;
        }

        // If tournament is already finished, show final standings and exit
        if (!activeTournament.hasMoreMatches()) {
            System.out.println("Tournament concluded. Final Standings:");
            activeTournament.printStandings();
            activeTournament = null;
            inTournamentMode = false;
            return;
        }

        // Get the next match from the tournament
        Match match = activeTournament.playNextMatch();
        System.out.println("Match: " + match.getPlayer1().getName() + " vs " + match.getPlayer2().getName());

        // Play this match using AI vs AI (hard mode for both)
        playMatchWithAI(match, activeTournament);

        // If finished after this match, end tournament mode
        if (!activeTournament.hasMoreMatches()) {
            System.out.println("Tournament concluded. Final Standings:");
            activeTournament.printStandings();
            activeTournament = null;
            inTournamentMode = false;
        } else {
            System.out.println("Type 'next' to continue to the next match.");
        }
    }

    /**
     * Creates a tournament from user command parts.
     * Expected format:
     *   create tournament <id> <p1> <p2> <p3> ...
     *
     * This method validates:
     * - tournament id must be an integer
     * - at least 3 players
     * - all players must exist in player store
     */
    private void handleTournamentCreation(String[] parts) {
        int tournamentId;
        try {
            tournamentId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid tournament ID. It must be an integer.");
        }

        if (parts.length < 6) {
            throw new IllegalArgumentException("Tournament needs at least 3 players to start.");
        }

        // Convert names into PlayerProfile objects
        PlayerProfile[] tournamentPlayers = new PlayerProfile[parts.length - 3];
        for (int i = 3; i < parts.length; i++) {
            String playerName = parts[i];
            PlayerProfile p = playerStore.getProfileObject(playerName);
            if (p == null) {
                throw new IllegalArgumentException("Player '" + playerName + "' does not exist in the player store.");
            } else {
                tournamentPlayers[i - 3] = p;
            }
        }

        // Create and store tournament
        Tournament tournament = new Tournament(tournamentId, tournamentPlayers);
        TournamentManager.addTournament(tournament);
    }

    /**
     * Drops a piece into the board for the current player.
     * Handles:
     * - invalid column numbers
     * - full column
     * - win/draw checks
     * - turn rotation (human vs human)
     * - AI result recording (human vs AI)
     */
    private void handleDrop(int col) {
        // Validate column range
        if (col < 0 || col >= board.getCols()) {
            throw new IllegalArgumentException("Unacceptable column number! Please try again.");
        }

        // Decide whose token is being dropped
        char currentToken;
        String currentName;

        if (vsAI) {
            // Human's turn in vs-AI mode
            currentToken = humanToken;
            currentName = "Human";
        } else {
            // Human vs Human: get current player from the queue
            Player current = turnQueue.peek();
            currentToken = current.token();
            currentName = current.name();
        }

        // Drop the token; board.drop returns the row where it landed
        int row = board.drop(col, currentToken);

        // If board.drop uses -1 to indicate "column full"
        if (row == -1) {
            System.out.println("That column is full. Try another one.");
            return;
        }

        // Push this move for undo support
        undoStack.push(new Move(row, col, currentToken));

        // If this move makes a 4-in-a-row, game ends
        if (board.isWinningMove(row, col)) {
            System.out.println("Congratulations! " + currentName + " (" + currentToken + ") wins!");
            gameOver = true;

            // If vs AI, record win for logged-in user
            if (vsAI) {
                recordVsAIResultForLoggedInUser(1);
            }
            return;
        }

        // If board is full, it is a draw
        if (board.isFull()) {
            System.out.println("It is a draw!");
            gameOver = true;

            // If vs AI, record draw for logged-in user
            if (vsAI) {
                recordVsAIResultForLoggedInUser(0);
            }
            return;
        }

        // Rotate turns only for Human vs Human
        if (!vsAI) {
            turnQueue.rotate();
        }
    }

    /**
     * Undo logic:
     * - In normal mode: undo one move and rotate back the turn
     * - In vs-AI mode: undo two moves (AI move + human move)
     */
    private void handleUndo() {
        // Undo is not allowed after game ends
        if (gameOver) {
            System.out.println("Undo is not allowed after the game ends. Type 'restart' to play again.");
            return;
        }

        // If stack is empty, nothing to undo
        if (undoStack.isEmpty()) {
            System.out.println("There is no move to undo.");
            return;
        }

        // Special rule: vs-AI mode undoes BOTH the AI move and the human move
        if (vsAI) {
            if (undoStack.size() < 2) {
                System.out.println("Not enough moves to undo in vs-AI mode.");
                return;
            }

            // Undo AI move first
            Move aiMove = undoStack.pop();
            board.undo(aiMove.row(), aiMove.col());

            // Undo human move
            Move humanMove = undoStack.pop();
            board.undo(humanMove.row(), humanMove.col());

            System.out.println("Undid your last move and the AI's last move.");
            board.print();
            return;
        }

        // Human vs Human: undo only last move and rotate back
        Move lastMove = undoStack.pop();
        board.undo(lastMove.row(), lastMove.col());
        turnQueue.rotate(); // rotate back so the same player can play again

        System.out.println("Last move undone. Back to " + turnQueue.peek().name() + ".");
        board.print();
    }

    /**
     * Reset everything back to a fresh new game.
     * Clears board, undo stack, turn queue, AI mode flags, tournament flags.
     */
    private void restart() {
        board.clear();
        undoStack.clear();
        turnQueue.clear();

        // Re-add default players
        turnQueue.enqueue(new Player("Player 1", 'X'));
        turnQueue.enqueue(new Player("Player 2", 'O'));

        // Reset AI mode
        vsAI = false;
        aiLevel = "";
        aiPlayer = null;
        humanToken = 'X';

        // Reset tournament mode
        inTournamentMode = false;
        activeTournament = null;
        pendingMatch = null;

        System.out.println("Game restarted!");
        board.print();
        gameOver = false;
    }

    /**
     * Shows hint information for the current player.
     * Uses the Hints class to compute safe/unsafe moves and a recommendation.
     */
    private void handleHint() {
        char currentToken;
        char opponentToken;
        String playerLabel;

        if (vsAI) {
            // Human vs AI: current is human, opponent is AI
            currentToken = humanToken;
            opponentToken = aiPlayer.getToken();
            playerLabel = "Human";
        } else {
            // Human vs Human: current from queue, opponent is next in queue
            Player current = turnQueue.peek();
            Player opponent = turnQueue.peekNext();

            currentToken = current.token();
            playerLabel = current.name();

            // If opponent is null (edge case), pick the opposite token manually
            if (opponent != null) opponentToken = opponent.token();
            else opponentToken = (currentToken == 'X') ? 'O' : 'X';
        }

        // Get hints and print them
        Hints hints = Hints.getHints(currentToken, opponentToken, board);

        System.out.println();
        System.out.println("=== HINT for " + playerLabel + " (" + currentToken + ") ===");
        hints.print();
        System.out.println();
    }

    /**
     * Plays one AI-vs-AI match completely (used for tournament).
     * Uses HARD AI for both sides.
     *
     * This does NOT use the main game board.
     * It creates a fresh matchBoard so tournament games do not affect your current game.
     */
    private void playMatchWithAI(Match match, Tournament tournament) {
        // New board for this match only
        Board matchBoard = new Board(ROWS, COLS, CONNECT);
        matchBoard.clear();

        // Hard AI for both sides
        AIPlayer aiPlayer1 = new AIPlayer('X');
        AIPlayer aiPlayer2 = new AIPlayer('O');

        // Player profiles from the match
        PlayerProfile player1 = match.getPlayer1();
        PlayerProfile player2 = match.getPlayer2();

        // True = player1's turn, false = player2's turn
        boolean player1Turn = true;

        System.out.println("----------------------------------");
        System.out.println("Starting match: "
                + player1.getName() + " (X) vs "
                + player2.getName() + " (O)");
        matchBoard.print();

        // Run until win or draw
        while (true) {
            // Decide which AI and which player is moving right now
            AIPlayer currentAI = player1Turn ? aiPlayer1 : aiPlayer2;
            PlayerProfile currentPlayer = player1Turn ? player1 : player2;

            // AI chooses a move and plays it
            int col = currentAI.hardMove(matchBoard);
            int row = matchBoard.drop(col, currentAI.getToken());

            System.out.println(currentPlayer.getName() + " (" + currentAI.getToken() + ") plays column " + col);
            matchBoard.print();

            // If this move wins the match
            if (matchBoard.isWinningMove(row, col)) {
                System.out.println(currentPlayer.getName() + " wins the match!");

                // Update tournament standings
                PlayerProfile loser = player1Turn ? player2 : player1;
                tournament.addWin(currentPlayer);
                tournament.addLoss(loser);
                return;
            }

            // If the board is full, it is a draw
            if (matchBoard.isFull()) {
                System.out.println("Match ended in a draw.");

                // Record draw for both players (overall stats)
                player1.recordOverallResult(0);
                player2.recordOverallResult(0);
                return;
            }

            // Switch turns
            player1Turn = !player1Turn;
        }
    }
}

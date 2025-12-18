import java.util.Random;

public class AIPlayer {

    private char token; //AI's piece
    private char opp; //opponent's piece
    private Random rand = new Random(); //random number generator

    public AIPlayer(char token) {
        this.token = token;
        if (token == 'X') {
            this.opp = 'O';
        } else {
            this.opp = 'X';
        }
    }

    public char getToken() {
        return token;
    }

    public int randomMove(Board board) {
        int cols = board.getCols(); //AI asks how many columns do we have?
        int chosenColumn;           //placeholder for the column that AI picks

        do {
            chosenColumn = rand.nextInt(cols);     //AI picks a random column
        } while (board.isColumnFull(chosenColumn)); // then we check if the chosen column is full. if it is Ai picks another

        return chosenColumn; //the method returns the final chosen column by AI
    }


    public int mediumMove(Board board) {

        int cols = board.getCols();

        //Step 1: Can AI win right away?
        for (int column = 0; column <cols; column++){
            if (!board.isColumnFull(column)){
                if (canWin(board, token, column)){
                    return column; //it finds the winning move
                }
            }
        }

        //Step2: can human win next? block it
        for (int column = 0; column < cols; column++){
            if (!board.isColumnFull(column)){
                if (canWin(board, opp, column)){
                    return column; //this is the column that blocks human win
                }
            }
        }

        //Step 3: we use depth-limited minimax algorithm with alpha beta pruning
        int bestScore = Integer.MIN_VALUE;
        int bestColumn = randomMove(board); // our fall back to  random
        int depthLimit = 4;

        for (int column = 0; column < cols; column++){
            if (!board.isColumnFull(column)){
                int row = board.drop(column, token);

                int score = minimax( board, depthLimit -1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

                board.undo(row, column);

                if(score > bestScore ){
                    bestScore = score;
                    bestColumn = column;
                }

            }
        }

        return bestColumn;

    }

    private boolean canWin(Board board, char testToken, int column) {
        // If the column is full, this move is not possible
        if (board.isColumnFull(column)){
            return false;
        }

        //simulate dropping the token in the column
        int row = board.drop(column, testToken);

        //if drop failed, it can't be winning
        if (row == -1){
            return false;
        }

        //check if the simulated move can win
        boolean wins = board.isWinningMove(row, column);

        //undo the simulated move so the real board is unchanged
        board.undo(row, column);

        return wins;
    }

    private boolean isSafeMove(Board board, int column) {

        //if AI cannot even play here, it is not safe
        if (board.isColumnFull(column)){
            return false;
        }

        //simulate AI move
        int aiRow = board.drop(column, token);

        //check if human can win after this move
        int cols = board.getCols();
        for (int c=0; c<cols; c++){
            if (!board.isColumnFull(c)){
                if (canWin(board, opp, c)){
                    board.undo(aiRow, column);
                    return false; //unsafe move
                }
            }
        }

        //undo AI move
        board.undo(aiRow, column);

        return true; //safe move
    }

    private int evaluate(Board board) {
        int cols = board.getCols();

        //If AI has a winning move
        for (int col=0; col<cols; col++){
            if (canWin(board, token, col)){
                return 1000;
            }
        }

        //If human has a winning move

        for (int col=0; col<cols; col++){
            if (canWin(board, opp, col)){
                return -1000;
            }
        }

        //otherwise neutral
        return 0;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizing) {

        // Base case
        if (depth == 0 || board.isFull()) {
            return evaluate(board);
        }

        int cols = board.getCols();

        // MAX node (AI's turn)
        if (maximizing) {

            int bestValue = Integer.MIN_VALUE;
            boolean prune = false;

            for (int col = 0; col < cols && !prune; col++) {
                if (!board.isColumnFull(col)) {

                    int row = board.drop(col, token);

                    //recursion part of the algorithm, also depth is reduced on every recursion (implemented depth-limited minimax)
                    int value = minimax(board, depth - 1, alpha, beta, false);

                    board.undo(row, col);

                    if (value > bestValue) {
                        bestValue = value;
                    }

                    if (value > alpha) {
                        alpha = value;
                    }

                    if (beta <= alpha) {
                        prune = true;
                    }
                }
            }
            return bestValue;
        }

        // MIN node (Human's turn)
        else {

            int bestValue = Integer.MAX_VALUE;
            boolean prune = false;

            for (int col = 0; col < cols && !prune; col++) {
                if (!board.isColumnFull(col)) {

                    int row = board.drop(col, opp);

                    //recursion part od the algorithm, also depth is reduced on every recursion (implemented depth-limited minimax)
                    int value = minimax(board, depth - 1, alpha, beta, true);

                    board.undo(row, col);

                    if (value < bestValue) {
                        bestValue = value;
                    }

                    if (value < beta) {
                        beta = value;
                    }

                    if (beta <= alpha) {
                        prune = true;
                    }
                }
            }
            return bestValue;
        }
    }


    public int hardMove(Board board) {

        int cols = board.getCols();

        //Step 1: Can AI winn rightaway?
        for (int column = 0; column < cols; column++){
            if (!board.isColumnFull(column)){
                if (canWin(board, token, column)){
                    return column;
                }
            }
        }

        //Step 2: Can human win in the next move? block that move
        for (int column = 0; column < cols; column++){
            if (!board.isColumnFull(column)){
                if (canWin(board, opp, column)){
                    return column;
                }
            }
        }

        //Step 3: choose safe move
        for (int column = 0; column < cols; column++){
            if (isSafeMove(board, column)){
                return column;
            }
        }

        //Fallback to random if no safe move exists
        return randomMove(board);
    }

}

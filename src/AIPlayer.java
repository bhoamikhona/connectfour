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
        // TEMPORARY: medium move behaves like random for now
        return randomMove(board);
    }

    public int hardMove(Board board) {
        // TEMPORARY: hard move behaves like random for now
        return randomMove(board);
    }

}

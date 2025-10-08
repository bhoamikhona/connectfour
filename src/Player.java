package connectfour;

/**
 * Represents a player in the Connect Four game.
 * Each player has a name and a token character ('X' or 'O').
 */
public class Player {
    private String name;
    private char token;

    public Player(String name, char token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public char getToken() {
        return token;
    }

    @Override
    public String toString() {
        return name + " (" + token + ")";
    }
}

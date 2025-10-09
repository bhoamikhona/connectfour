import java.util.LinkedList;

/**
 * Hints class provides analysis for safe and unsafe moves.
 * It uses LinkedLists to store column indices that are safe or unsafe,
 * and recommends one column to play (favoring the center).
 */
public class Hints {
    public LinkedList<Integer> safeCols;
    public LinkedList<Integer> unsafeCols;
    public int recommendedCol;

    public Hints() {
        safeCols = new LinkedList<>();
        unsafeCols = new LinkedList<>();
        recommendedCol = -1;
    }

    public void print() {
        System.out.println("Safe columns: " + safeCols);
        System.out.println("Unsafe columns: " + unsafeCols);
        System.out.println("Recommended column: " + recommendedCol);
    }
}

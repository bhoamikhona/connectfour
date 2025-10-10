
/**
 * Hints class provides analysis for safe and unsafe moves.
 * It uses our custom LinkedList to store column indices that are safe or unsafe,
 * and recommends one column to play (favoring the center).
 */
public class Hints {
    public LinkedList safeCols;
    public LinkedList unsafeCols;
    public int recommendedCol;

    public Hints() {
        safeCols = new LinkedList();
        unsafeCols = new LinkedList();
        recommendedCol = -1;
    }

    public void print() {
        System.out.println("Safe columns: " + safeCols);
        System.out.println("Unsafe columns: " + unsafeCols);
        System.out.println("Recommended column: " + recommendedCol);
    }
}

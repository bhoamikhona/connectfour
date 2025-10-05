/**
 * Circular queue for Player objects with safe rotate (dequeue + enqueue).
 */
public class TurnQueue {
    private Player[] data;
    private int head = 0;   // index of current front
    private int tail = 0;   // index just after the last element
    private int size = 0;   // number of stored players

    public TurnQueue(int capacity) { this.data = new Player[capacity]; }

    /**
     * TODO: You need to implement the size, isEmpty, isFull methods.
     * The return values will need to be fixed.
     * @return
     */
    public int size() { return 0; }
    public boolean isEmpty() { return true; }
    public boolean isFull() { return true; }

    public void enqueue(Player p) {}

    /**
     * TODO: Uncomment and implement the peek and dequeue methods.
     */
    // public Player peek() {}

    // public Player dequeue() {}

    /** TODO: Implement the rotate and clear methods */
    public void rotate() {}

    public void clear() {}
}

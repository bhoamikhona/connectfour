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

    /**
     * Returns how many players are in the queue
     * @return current number of elements
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the queue has no elements
     * @return true when size is 0, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if the queue is full.
     * @return true when size equals capacity, false otherwise
     */
    public boolean isFull() {
        return size == data.length;
    }

    /**
     * Adds a player at the back (tail) of the queue
     * @param p player to add
     * @throws IllegalStateException if the queue is full
     */
    public void enqueue(Player p) {
        if (p == null) return;

        if (isFull()) throw new IllegalStateException("Queue is full");

        // store the new player at the current tail position
        data[tail] = p;

        // move the tail forward one slot, wrapping to 0
        tail = (tail + 1) % data.length;

        // increment the size of the queue
        size++;
    }

    /**
     * TODO: Uncomment and implement the peek and dequeue methods.
     */

    /**
     * Returns the front player without removing it.
     * @return front player, or null if empty
     */
     public Player peek() {
         return isEmpty() ? null : data[head];
     }

    /**
     * Removes and returns the front player.
     * @return removed player, or null if empty
     */
     public Player dequeue() {
         if (isEmpty()) return null;

         // access the player at the current head position
         Player p = data[head];

         // assign head to the next player in the queue
         head = (head + 1) % data.length;

         // decrease the size of the queue
         size--;

         // return the removed player
         return p;
     }

    /** TODO: Implement the rotate and clear methods */

    /**
     * Moves the front player to the back.
     * No effect if there are less than 2 players.
     */
    public void rotate() {
        if (size < 2) return;

        // remove the player at the front and store it in "first"
        Player first = dequeue();

        // put the player stored in "first" at the tail of the queue
        enqueue(first);
    }

    /**
     * Removes all players and resets the queue.
     * Capacity does not change.
     */
    public void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = null;
        }

        head = 0;
        tail = 0;
        size = 0;
    }

    /**
     * Returns the next player in the queue (after the front)
     * without modifying the queue order.
     * @return next player, or null if there is only one or none
     */
    public Player peekNext() {
        if (size < 2) return null;
        int nextIndex = (head + 1) % data.length;
        return data[nextIndex];
    }

}

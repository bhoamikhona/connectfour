/**
 * TODO:
 * Array backed stack for Move objects (LIFO). Implement from scratch
 */
public class MoveStack {

    // NOTE:
    // In this design top is the index of the last element, not the count
    // When stack is empty, top == -1
    // number of elements = -1 + 1 = 0
    // after first push, top = 0 so, the size is 0 + 1 = 1
    // after kth push, top = k-1 so, the size is (k - 1) + 1 = k

    private Move[] data;
    private int top = -1; // index of the current element; -1 means empty

    public MoveStack(int capacity) {
        this.data = new Move[capacity];
    }

    /**
     * TODO: Implement the pushing of the stack
     * 
     * @param m
     */

    /**
     * Pushes a move onto the top of the stack
     * Does nothing is {@param m} is null. Throws if the stack is full.
     *
     * @param m the move to push (ignored if null)
     * @throws IllegalStateException if the stack is already full
     */
    public void push(Move m) {
        if (m == null)
            return;

        if (top + 1 == data.length) {
            throw new IllegalStateException("MoveStack is full");
        }

        data[++top] = m;
    }

    /** TODO: Uncomment this method and implement the popping of the stack */
    /**
     * Pops and returns the top move.
     * If the stack is empty, returns null
     *
     * @return the removed top move, or null if empty
     */
    public Move pop() {

        if (isEmpty())
            return null;

        Move m = data[top];
        data[top] = null;
        top--;

        return m;
    }

    /**
     * TODO: You need to implement the isEmpty method
     * 
     * @return
     */

    /**
     * Returns true if the stack has no elements.
     *
     *
     * @return true when top == -1, false otherwise
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     *
     * TODO: Implement the clear method
     */

    /**
     * Removes all moves and resets the stack.
     */
    public void clear() {

        // Setting each value of the stack to null
        for (int i = 0; i <= top; i++) {
            data[i] = null;
        }

        // setting the top value to -1
        top = -1;
    }

    /**
     * TODO: You need to implement the size method
     * 
     * @return
     */

    /**
     * Returns how many moves are in the stack.
     * @return current number of elements (equal to top + 1)
     */
    public int size() {
        return top + 1;
    }
}

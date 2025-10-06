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
    public void push(Move m) {
        if (m == null)
            return;

        if (top + 1 == data.length) {
            throw new IllegalStateException("MoveStack is full");
        }

        data[++top] = m;
    }

    /** TODO: Uncomment this method and implement the popping of the stack */
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
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     *
     * TODO: Implement the clear method
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
    public int size() {
        return top + 1;
    }
}

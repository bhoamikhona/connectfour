/** TODO:
 * Array backed stack for Move objects (LIFO). Implement from scratch
 */
public class MoveStack {
    private  Move[] data;
    private int top = -1;

    public MoveStack(int capacity) {
        this.data = new Move[capacity];
    }

    /**
     * TODO: Implement the pushing of the stack
     * @param m
     */
    public void push(Move m) {}

    /** TODO: Uncomment this method and implement the popping of the stack*/
    // public Move pop() {
    //     return m;
    // }

    /**
     * TODO: You need to implement the isEmpty method
     * @return
     */
    public boolean isEmpty() { return true; }

    /**
     * TODO: Implement the clear method
     */
    public void clear() {}


    /**
     * TODO: You need to implement the size method
     * @return
     */
    public int size() { return 0;}
}

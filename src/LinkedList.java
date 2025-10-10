/**
 * Custom singly linked list implementation for integers.
 */
public class LinkedList {
    //inner Node class
    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private Node tail;
    private int size;

    //Add value to end of list
    public void add(int value) {
        Node newNode = new Node(value);
        if (head == null) {    //list is empty
            head = newNode;
            tail = newNode;
        } else {     //list already has stuff in it
            tail.next = newNode; // connect old tail to new node
            tail = newNode;  // move tail to the new node
        }
        size++;
    }

   //Get value at index (it walks through the chain until it reaches the index we are looking for)
    public int get(int index) {
        Node current = head;
        int i = 0;
        while (current != null) {
            if (i == index) return current.value;
            current = current.next;
            i++;
        }
        throw new IndexOutOfBoundsException("Index out of range");
    }

    //gives us the value of the very first element
    public int getFirst() {
        if (head == null) throw new IllegalStateException("Empty list");
        return head.value;
    }

    //Return true if list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    //Return number of elements
    public int size() {
        return size;
    }

    //Nicely print contents (builds a text version to look nice when we print)
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node current = head;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) sb.append(" -> ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}

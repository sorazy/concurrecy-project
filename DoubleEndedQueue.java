/**
 * A sequential DEqueue implemenation to use as a baseline for our data
 * structure project. This code will be used in conjunction with a concurrent
 * library to add fine grained locking to the functions. 
 * @see Node   
 */
public class DoubleEndedQueue<T> {
    // Front of the queue
    private Node<T> head;
    // End of the queue
    private Node<T> tail;
    private int size;
    
    // Constructs an empty queue
    public DoubleEndedQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * This function adds an element to the front of the queue making it the
     * new head. 
     * @param data to insert into the queue
     */ 
    public void push_front(T data) {
    	// store the current head of the queue
        Node<T> prevHead = this.head;
        // create the new head, with type data, prev = null, next = head
        Node<T> newHead = new Node<>(data, null, prevHead);
        // set the new node as the head 
        this.head = newHead;
        // check if the queue is empty
        if(prevHead == null) 
        	this.tail = this.head;	// initialize tail to head
        else 
        	prevHead.setPrev(newHead);	// else set prevHead.prev to the new head
        this.size++;
        return;
    }

    public void push_back(T data) {
    	// store the current tail of the queue
    	Node<T> prevTail = this.tail;
        // create the new tail, with type data, prev = tail, next = null
        Node<T> newTail = new Node<>(data, prevTail, null);
        // set the new node as the tail 
        this.tail = newTail;
        // check if the queue is empty
        if(prevTail == null)
        	this.head = this.tail;	// initialize tail to head
        else
        	prevTail.setNext(newTail);	// else set prevHead.prev to the new head
        this.size++;
        return;
    }

    public T pop_front() {
    	// if the queue is empty return null
    	if(this.head == null)
    		return null;
    	// store the result of the front pop
        Node<T> result = this.head;
        // check if there are more items in the queue
        if(result.getNext() != null) {
        	// physically remove the item from the list by setting the head = head.next
        	this.head = this.head.getNext();
        	// mark the new head's previous field to null  indicating the end of the list
        	this.head.setPrev(null);
        }
        else {	// list is now empty
        	// set the head and tail to null
        	this.head = null;
        	this.tail = null;
        }
        this.size--;
        // return the popped node's data
        return result.getData();
    }

    public T pop_back() {
    	// if the queue is empty return null
    	if(this.tail == null)
    		return null;
    	// store the result of the back pop
        Node<T> result = this.tail;
        // check if there are more items in the queue
        if(result.getPrev() != null) {
        	// physically remove the item from the list by setting the tail = tail.prev
        	this.tail = this.tail.getPrev();
        	// mark the new tail's next field to null indicating the end of the list
        	this.tail.setNext(null);
        }
        else {	// list is now empty
        	// set the head and tail to null
        	this.head = null;
        	this.tail = null;
        }
        this.size--;
        return result.getData();
    }
    
    /** tests if the queue is empty, this occurs if this.head is null 
     * @return boolean
     */
    public boolean isEmpty() { return (this.head == null) ? true : false; }

    /**
     * returns size field of the object
     * @return number of elements
     */
    public int size() { return this.size; }

    public String toString() {
        String output = "[head]->";
        Node<T> walker = this.head;
        while(walker != null) {
            output += "["+ walker.getData().toString() + "]->";
            walker = walker.getNext();
        }
        output += "[tail]";
        return output;
    }
}

/**
 * This Node<T> class implements a double linked list using a previous and next
 * object reference to traverse the list. This implemenation will make creating
 * an unbounded DEqueue object much easier than using an array. 
 */ 
class Node<T> {
    private T data;
    private Node<T> prev;
    private Node<T> next;

    /**
    * Constructor to create a node storing x and setting the next field to null indicating the
    * end of the list.
    * @param x
    */
    public Node(T x) {
        this.data = x;
        this.prev = null;
        this.next = null;
    }

    /**
    * Constructor to create a node storing x and setting the next field to
    * a specified next value.
    * @param x
    */
    public Node(T x, Node<T> prev, Node<T> next) {
        this.data = x;
        this.prev = prev;
        this.next = next;
    }

    /** Simple getter function
    * @return data */
    public T getData() { return this.data; }

    /** Simple getter function
    * @return next */
    public Node<T> getNext() { return this.next; }

    /** Simple getter function
    * @return next */
    public Node<T> getPrev() { return this.prev; }

    public void setNext(Node<T> n) { this.next = n; }

    public void setPrev(Node<T> p) { this.prev = p; }

    // use the toString of the given data
    public String toString() { return data.toString(); }
}


public class DoubleEndedQueue<T> {
    private Node<T> head;
    private Node<T> tail;
    
    public DoubleEndedQueue() {
        this.head = null;
        this.tail = null;
    }

    public void push_front(T data) {
    	Node<T> head = this.head;
        Node<T> newHead = new Node<>(data, null, head);
        this.head = newHead;
        if(this.tail == null)
        	this.tail = this.head;
        if(head != null)
        	head.setPrev(newHead);
        return;
    }

    public void push_back(T data) {
    	Node<T> tail = this.tail;
        Node<T> newTail = new Node<>(data, tail, null);
        this.tail = newTail;
        if(this.head == null)
        	this.head = this.tail;
        if(tail != null)
        	tail.setNext(newTail);
        return;
    }

    public T pop_front() {
    	if(this.head == null)
    		return null;
        Node<T> result = this.head;
        if(result.getNext() != null) {
        	this.head = this.head.getNext();
        	this.head.setPrev(null);
        }
        else {	// list is now empty
        	this.head = null;
        	this.tail = null;
        }
        return result.getData();
    }

    public T pop_back() {
    	if(this.tail == null)
    		return null;
        Node<T> result = this.tail;
        if(result.getPrev() != null) {
        	this.tail = this.tail.getPrev();
        	this.tail.setNext(null);
        }
        else {	// list is now empty
        	this.head = null;
        	this.tail = null;
        }
        return result.getData();
    }
    
    public boolean isEmpty() {
    	if(this.head == null)
    		return true;
    	return false;
    }

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

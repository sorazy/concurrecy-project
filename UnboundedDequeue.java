/**
 * Group __ Data Structure Project (Sequential Version)
 * A sequential implemnetation of an Unbounded Double Ended Queue.
 * This code will be used with a concurrency wrapper to perform efficiency
 * testing with our concurrent non-blocking dequeue.
 * Algorithm Summary : 
 * This dequeue implememntation is effectively a double linked list of bounded
 * array based dequeues. When an array becomes full a new bounded dequeue is
 * appended to the head or tail of the UnboundedDequeue.
 * In order to keep high efficiency in push/pop operations a left and right
 * hint is stored keeping track of the edge bounded dequeus and the index of
 * the next element in the array.
 * @author Ryan Dozier, Soliman Alnaizy, Natasha Zdravkovic
 * 
 */ 
@SuppressWarnings("unchecked")
public class UnboundedDequeue<T> {
	/**
     * This class is used to store hints on whre the edge elemnt are within the
     * data structure. 
     * @param queue the bounded array in which the last node is located.
     * @param loc the location of the last node in the array.
     */ 
	class HLM_Hint {
		public HLM_dequeue queue;
		public int loc;
	
		public HLM_Hint(HLM_dequeue queue, int location) {
			this.queue = queue;
			this.loc = location;
		}
	}
	
    // Size of each bounded dequeue.
	private final int HLM_SIZE;
    // Constant value marking items to the left as null;
    private T leftNull;
    // Constant marking items to the right of the end null.
    private T rightNull;
    // the hint to the left most (front) node in the dequeue.
    private HLM_Hint leftHint;
    // the hint of the right most (back) node in the dequeue.
    private HLM_Hint rightHint;

    /**
     * No arg Constructor, if no size is specified each contiguous dequeue is
     * set to size = 100. 
     */ 
	public UnboundedDequeue() {
		this.HLM_SIZE = 100;
		this.leftNull = (T) new Object();
		this.rightNull = (T) new Object();
		int half = (HLM_SIZE % 2 == 0) ? HLM_SIZE / 2 : (HLM_SIZE / 2) + 1;
		HLM_dequeue queue = new HLM_dequeue(half);
		this.leftHint = new HLM_Hint(queue, half - 1);
		this.rightHint = new HLM_Hint(queue, half);;
	}

    /**
     * A size specified constructor. 
     * @param size of the bounded arrays
     */ 
	public UnboundedDequeue(int size) {
		this.HLM_SIZE = size;
		this.leftNull = (T) new Object();
		this.rightNull = (T) new Object();
		int half = (HLM_SIZE % 2 == 0) ? HLM_SIZE / 2 : (HLM_SIZE / 2) + 1;
		HLM_dequeue queue = new HLM_dequeue(half);
		this.leftHint = new HLM_Hint(queue, half - 1);
		this.rightHint = new HLM_Hint(queue, half);;
	}
	
    /**
     * Pushes an item onto the left (front) of the dequeue.
     * @param data the item to place into the dequeue.
     */ 
	public void push_left(T data) {
		this.leftHint.queue.push_left(data, this.leftHint.loc);
		this.leftHint.loc--;
		
		// if the current array is full, create another array.
		if(this.leftHint.loc < 0) {
            // create a new bounded dequeue, set prev= null, next= leftmost 
			HLM_dequeue newQueue = new HLM_dequeue(null, this.leftHint.queue, this.leftNull);
			// change the previous of the current leftmost to the new dequeue.
            this.leftHint.queue.prev = newQueue;
            // change the current left hint to the newly created queue making
            // it the new leftmost queue.
			this.leftHint.queue = newQueue;
            // set the new leftHint location
			this.leftHint.loc = HLM_SIZE - 1;
		}
		return;
	}
	 
    /**
     * Pushes an item onto the right (end) of the dequeue.
     * @param data the item to place into the dequeue.
     */ 
	public void push_right(T data) {
		this.rightHint.queue.push_left(data, this.rightHint.loc);
		this.rightHint.loc++;
		
		/// if the current array is full, create another array.
		if(this.rightHint.loc == HLM_SIZE) {
			// create a new bounded dequeue, set prev= rightmost, next= null 
            HLM_dequeue newQueue = new HLM_dequeue(this.rightHint.queue, null,  this.rightNull);
			// change the next of the current rightmost to the new dequeue.
            this.rightHint.queue.next = newQueue;
			 // change the current right hint to the newly created queue making
            // it the new rightmost queue.
            this.rightHint.queue = newQueue;
            // set the new rightmost hint location
			this.rightHint.loc = 0;
		}
		
		return;
	}
	
    /**
     * Returns and removes the left most item in the dequeue
     * @return the leftmost item in the dequeue
     */ 
	public T pop_left() { 
        // look to the next element
		this.leftHint.loc++;
        // check if the hint is past the end of the array
		if(this.leftHint.loc == HLM_SIZE) {
			if(this.leftHint.queue.next == null)
				return null;
            // set the left hint to the next dequeue
			this.leftHint.queue = this.leftHint.queue.next;
			// look at the first index in the array.
            this.leftHint.loc = 0;
		}
        // pop the element from the dequeue
		T result = this.leftHint.queue.pop_left(this.leftHint.loc);
		return result;
	}
	
    /**
     * Returns and removes the right most item in the dequeue
     * @return the rightmost item in the dequeue
     */
	public T pop_right() { 
        // look to the previous element
		this.rightHint.loc--;
        // check if the new loc is out of bounds
		if(this.rightHint.loc < 0) {
			if(this.rightHint.queue.prev == null)
				return null;
            // set the right hint to the previous dequeue
			this.rightHint.queue = this.rightHint.queue.prev;
            // set the location to the last element in the queue
			this.rightHint.loc = HLM_SIZE - 1;
		}
        // pop the element form the dequeue
		T result = this.rightHint.queue.pop_right(this.rightHint.loc);
		return result;
	}
	
    /**
     * This class creates an array based bounded dequeue. It also contains
     * a previous and next Object reference to create the dequeue into
     * a doubly linked list.
     */ 
	class HLM_dequeue {
        // The array to store data in
		private HLM_Slot<T>[] array;
        // next dequeue object reference
		private HLM_dequeue next;
        // previous dequeue object reference
		private HLM_dequeue prev;
		
        /**
         * Constructor to create the dequeue and initialize half the array to
         * leftNull and the other half to rightNull. This will be used when
         * creating the initial array.
         */ 
		private HLM_dequeue(int half) {
			this.array = (HLM_Slot<T>[])new HLM_Slot<?>[HLM_SIZE];
			this.next = null;
			this.prev = null;
			// Set the left half to leftNull
	        for(int i = 0; i < half; i++) {
	            this.array[i] = new HLM_Slot<>((T) leftNull, 0);
	        }
	        // Right half is set to rightNull
	        for(int i = HLM_SIZE - 1; i >= half; i--) {
	            this.array[i] = new HLM_Slot<>((T) rightNull, 0);
	        }
	        
		}
		
        /**
         * This constructor is used when adding additional arrays to the data
         * structure. 
         * @param prev this will be set to rightHint.queue when adding to
         * the right and null when adding to the left
         * @param next this will be set to leftHint.queue when adding to the
         * left and null wehn adding to the right.
         * @param init when adding to the left pass leftNull, when to the right
         * pass rightNull
         */
		private HLM_dequeue(HLM_dequeue prev, HLM_dequeue next, T init) {
			this.array = (HLM_Slot<T>[])new HLM_Slot<?>[HLM_SIZE];
	        this.next = next;
	        this.prev = prev;
	        // initialize to rightNull, or leftNull
	        for(int i = 0; i < HLM_SIZE; i++) 
	            this.array[i] = new HLM_Slot<>(init, 0);
	    }
		
        /**
         * This function places an item into the array at the specified
         * location.
         * @param data
         * @param loc
         */ 
	    public void push_left(T data, int loc) {
	    	this.array[loc].count++;
	    	this.array[loc].value = data;
	        return;
	    }
	    
        /**
         * This function places an item into the array at the specified
         * location.
         * @param data
         * @param loc
         */
	    public void push_right(T data, int loc) {
	    	this.array[loc].count++;
	    	this.array[loc].value = data;
	        return;
	    }
		
        /**
         * Pops an item from the left, and returns the value
         * @return left most item
         */ 
		public T pop_left(int loc) {
			this.array[loc].count++;
            // Store the result
			T result = this.array[loc].value;
            // Set the location to leftNull for pysical removal
			this.array[loc].value = leftNull;
			return result;
		}
		
        /**
         * Pops an item from the right, and returns the value
         * @return the right most value
         */ 
		public T pop_right(int loc) { 
			this.array[loc].count++;
            // Store the result
			T result = this.array[loc].value;
            // set its location ot rightnull for physical removal
			this.array[loc].value = rightNull;
			return result;
		}
	}
}

/**
 * The slot for each array. Count will be utilized in the concurrent version to
 * verify CAS has succeeded
 */ 
class HLM_Slot<T> { 
	public T value; 
	public int count; 

	public HLM_Slot(T data, int count) { 
		this.value = data; 
		this.count = count; 
	} 
}

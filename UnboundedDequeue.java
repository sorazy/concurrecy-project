
@SuppressWarnings("unchecked")
public class UnboundedDequeue<T> {
	
	class HLM_Hint {
		public HLM_dequeue queue;
		public int loc;
		
		public HLM_Hint(HLM_dequeue queue, int location) {
			this.queue = queue;
			this.loc = location;
		}
	}
	
	private final int HLM_SIZE;
    private T leftNull;
    private T rightNull;
    private HLM_Hint leftHint;
    private HLM_Hint rightHint;

	
	public UnboundedDequeue(int size) {
		this.HLM_SIZE = size;
		this.leftNull = (T) new Object();
		this.rightNull = (T) new Object();
		int half = (HLM_SIZE % 2 == 0) ? HLM_SIZE / 2 : (HLM_SIZE / 2) + 1;
		HLM_dequeue queue = new HLM_dequeue(half);
		this.leftHint = new HLM_Hint(queue, half - 1);
		this.rightHint = new HLM_Hint(queue, half);;
	}
	
	public void push_left(T data) {
		this.leftHint.queue.push_left(data, this.leftHint.loc);
		this.leftHint.loc--;
		
		// if the current array is full
		if(this.leftHint.loc < 0) {
			HLM_dequeue newQueue = new HLM_dequeue(null, this.leftHint.queue, this.leftNull);
			this.leftHint.queue.prev = newQueue;
			this.leftHint.queue = newQueue;
			this.leftHint.loc = HLM_SIZE - 1;
		}
		return;
	}
	
	public void push_right(T data) {
		this.rightHint.queue.push_left(data, this.rightHint.loc);
		this.rightHint.loc++;
		
		// if the current array is full
		if(this.rightHint.loc == HLM_SIZE) {
			HLM_dequeue newQueue = new HLM_dequeue(this.rightHint.queue, null,  this.rightNull);
			this.rightHint.queue.next = newQueue;
			this.rightHint.queue = newQueue;
			this.rightHint.loc = 0;
		}
		
		return;
	}
	
	public T pop_left() { 
		this.leftHint.loc++;
		if(this.leftHint.loc == HLM_SIZE) {
			if(this.leftHint.queue.next == null)
				return null;
			this.leftHint.queue = this.leftHint.queue.next;
			this.leftHint.loc = 0;
		}
		T result = this.leftHint.queue.pop_left(this.leftHint.loc);
		return result;
	}
	
	public T pop_right() { 
		this.rightHint.loc--;
		if(this.rightHint.loc < 0) {
			if(this.rightHint.queue.prev == null)
				return null;
			this.rightHint.queue = this.rightHint.queue.prev;
			this.rightHint.loc = HLM_SIZE - 1;
		}
		T result = this.rightHint.queue.pop_right(this.rightHint.loc);
		return result;
	}
	
	class HLM_dequeue {
		private HLM_Slot<T>[] array;
		private HLM_dequeue next;
		private HLM_dequeue prev;
		
		public HLM_dequeue(int half) {
			this.array = (HLM_Slot<T>[])new HLM_Slot<?>[HLM_SIZE];
			this.next = null;
			this.prev = null;
				     
	        for(int i = 0; i < half; i++) {
	            this.array[i] = new HLM_Slot<>((T) leftNull, 0);
	        }
	        
	        for(int i = HLM_SIZE - 1; i >= half; i--) {
	            this.array[i] = new HLM_Slot<>((T) rightNull, 0);
	        }
	        
		}
		
		private HLM_dequeue(HLM_dequeue prev, HLM_dequeue next, T init) {
			this.array = (HLM_Slot<T>[])new HLM_Slot<?>[HLM_SIZE];
	        this.next = next;
	        this.prev = prev;
	     
	        for(int i = 0; i < HLM_SIZE; i++) 
	            this.array[i] = new HLM_Slot<>(init, 0);
	    }
		
	    public void push_left(T data, int loc) {
	    	this.array[loc].count++;
	    	this.array[loc].value = data;
	        return;
	    }
	    
	    public void push_right(T data, int loc) {
	    	this.array[loc].count++;
	    	this.array[loc].value = data;
	        return;
	    }
		
		public T pop_left(int loc) {
			this.array[loc].count++;
			T result = this.array[loc].value;
			this.array[loc].value = leftNull;
			return result;
		}
		
		public T pop_right(int loc) { 
			this.array[loc].count++;
			T result = this.array[loc].value;
			this.array[loc].value = rightNull;
			return result;
		}
	}
}


class HLM_Slot<T> { 
	public T value; 
	public int count; 

	public HLM_Slot(T data, int count) { 
		this.value = data; 
		this.count = count; 
	} 
}
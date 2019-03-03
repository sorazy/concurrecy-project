public class HLMdequeue<T> {
	private final int HLM_SIZE;
	private HLM_Slot<T>[] array;
	private int half;
    private T leftNull;
    private T rightNull;

	@SuppressWarnings("unchecked")
	public HLMdequeue(int size) {
		this.HLM_SIZE = size;
		this.array = (HLM_Slot<T>[])new HLM_Slot<?>[HLM_SIZE];
        this.leftNull = (T) new Object();
        this.rightNull = (T) new Object();
        this.half = (this.HLM_SIZE % 2 == 0) ? this.HLM_SIZE / 2 : (this.HLM_SIZE / 2) + 1;
     
        for(int i = 0; i < this.half; i++) {
            this.array[i] = new HLM_Slot<>(this.leftNull, 0);
        }
        
        for(int i = this.HLM_SIZE - 1; i >= this.half; i--) {
            this.array[i] = new HLM_Slot<>(this.rightNull, 0);
        }
    }
    
    public T pop_left() {
    	int index = 0;
        T result = null;
        
        // finds the left most element
        while(index < this.HLM_SIZE && this.array[index].value == this.leftNull ) 
        	index++;
        
        if(index == this.HLM_SIZE || this.array[index].value == this.rightNull) {
        	if(index == this.HLM_SIZE)
        		this.rebalance();
        	return null;
        }
        
        this.array[index].count++;
        result = this.array[index].value;
        this.array[index].value = this.leftNull;
        
        return result;
    }

    public T pop_right() {
    	int index = this.HLM_SIZE - 1;
        T result = null;
        
        // finds the right most element
        while(index >= 0 && this.array[index].value == this.rightNull) 
        	index--;
        
        if(index == -1 || this.array[index].value == this.leftNull) {
        	if(index == -1)
        		this.rebalance();
        	return null;
        }
        	
       
        this.array[index].count++;
        result = this.array[index].value;
        this.array[index].value = this.rightNull;
        
        return result;
    }

    public void push_left(T data) throws DequeueFullException {
    	int index = 0;
    	if(this.array[index].value != this.leftNull)
    		throw new DequeueFullException("left full");
    	while(this.array[index + 1].value == this.leftNull)
    		index++;
    	this.array[index].count++;
    	this.array[index].value = data;
        return;
    }
    
    public void push_right(T data) throws DequeueFullException {
    	int index = this.HLM_SIZE - 1;
    	if(this.array[index].value != this.rightNull)
    		throw new DequeueFullException("right full");
    	while(this.array[index - 1].value == this.rightNull)
    		index--;
    	this.array[index].count++;
    	this.array[index].value = data;
        return;
    }
    
    private void rebalance() {
        for(int i = 0; i < this.half; i++) {
            this.array[i] = new HLM_Slot<>(this.leftNull, 0);
        }
        
        for(int i = this.HLM_SIZE - 1; i >= this.half; i--) {
            this.array[i] = new HLM_Slot<>(this.rightNull, 0);
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


@SuppressWarnings("serial")
class DequeueFullException extends Exception { 
    public DequeueFullException(String errorMessage) {
        super(errorMessage);
    }
}


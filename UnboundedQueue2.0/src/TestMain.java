import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMain {
	public static UnboundedDequeue<String> dequeue;
	public static final int num_threads = 5;

	public static void main(String[] args) throws InterruptedException {
		dequeue = new UnboundedDequeue<>();
		ExecutorService threadPool;
		long startTime, estimatedTime;
		double seconds;
		threadPool = Executors.newFixedThreadPool(num_threads);
		startTime = System.nanoTime(); 
		
		boolean useThreads = true;
		
		if(!useThreads) {
			for(int i = 0; i < 1000000; i++) {
				switch(i % 4) {
					case 0 :
						dequeue.pop_left();
						dequeue.pop_left();
						break;
					case 1 : 
						dequeue.push_left("1");
						dequeue.push_left("3");
						break;
					case 2 : 
						dequeue.push_right("2");
						dequeue.push_right("4");
						break;
					case 3 : 
						dequeue.pop_right();
						break;
					default :;
						
				}
			}
		}


		
		if(useThreads) {
			for(int j = 0; j < num_threads; j++) {
				threadPool.execute(new QueueThread(j));
			}
			threadPool.shutdown();
	
			while (!threadPool.isTerminated()) {
				Thread.sleep(1);
	        }
		}
        
		estimatedTime = System.nanoTime() - startTime;
		seconds = estimatedTime / 1e9;
		System.out.println(" Execution Time : " + seconds);
	}

    private static class QueueThread implements Runnable {
    	private int i;


    	public QueueThread(int i) {
    		this.i = i;

    	}
    	public void run() {
    		int n;
    		int numOps = 1000;
    		for(n = 0; n < numOps; n++) {
        		dequeue.push_left("This Program is Frustrating");
        		dequeue.push_right("So many components");
        		dequeue.push_right("So many infinite loops");
        		dequeue.push_left("monkaS");
        		System.out.println(dequeue.pop_left());
        		System.out.println(dequeue.pop_left());
        		System.out.println(dequeue.pop_right());
        		System.out.println(dequeue.pop_right());
//        		dequeue.pop_left();
//        		dequeue.pop_left();
//        		dequeue.pop_right();
//        		dequeue.pop_right();
    		}
    	}
    }
}

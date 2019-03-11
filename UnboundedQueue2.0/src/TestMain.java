import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMain {
	public static UnboundedDequeue<Integer> dequeue;
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
						System.out.println(dequeue.pop_left());
						System.out.println(dequeue.pop_left());
						break;
					case 1 : 
						dequeue.push_left(i);
						break;
					case 2 : 
						dequeue.push_right(i);
						break;
					case 3 : 
						System.out.println(dequeue.pop_right());
						break;
					default :;
						
				}
			}
		}


		
		if(useThreads) {
			for(int j = 0; j < num_threads; j++) {
				threadPool.execute(new QueueThread(j));
			}
			System.out.println("Threads started");
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
    		int numOps = 10000;
    		for(n = 0; n < numOps; n++) {
        		dequeue.push_left(1);
        		dequeue.push_right(i);
        		dequeue.push_right(i);
        		dequeue.push_left(1);
        		System.out.println(dequeue.pop_left());
        		System.out.println(dequeue.pop_left());
        		System.out.println(dequeue.pop_right());
        		System.out.println(dequeue.pop_right());
    		}
    	}
    }
}

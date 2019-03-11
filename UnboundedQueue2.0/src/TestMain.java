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



		
		for(int j = 0; j < num_threads; j++) {
			threadPool.execute(new QueueThread(j));
		}
		System.out.println("Threads started");
		threadPool.shutdown();

		while (!threadPool.isTerminated()) {
			Thread.sleep(1);
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
        		dequeue.pop_left();
        		//dequeue.pop_right();
    		}
    	}
    }
}

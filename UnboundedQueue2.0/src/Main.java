import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Thread
{
    public static UnboundedDequeue<Integer> q = new UnboundedDequeue<>();
    public static AtomicInteger counter = new AtomicInteger(0);
    public static final int CALLS = 10000, THREADS = 8;
    public static int [] pusharray = new int[CALLS];
    public static int [] poparray = new int[CALLS];
    public static int [] thread_array = {1, 2, 4, 8};

    public void run()
    {
        for (int i = counter.getAndIncrement(); i < CALLS; i = counter.getAndIncrement())
        {
            double r1 = Math.random();
            double r2 = Math.random();

            if (r1 >= 0.50)
            {
                if (r2 >= 0.50)
                    q.push_left(i);
                else
                    q.push_right(i);                    
            }
            else try
            {
                if (r2 >= 0.50)
                    q.pop_left();
                else
                    q.pop_right();
            }
            catch(Exception e)
            {
                ;
            }
        }
    }

    public static void main(String [] args) throws Exception
    {
        for (int k = 0; k < thread_array.length; k++)
        {
            Thread [] t = new Thread[THREADS];

            // To calculate the average
            long total = 0;
    
            for (int j = 0; j < 1000; j++)
            {
                long start = System.nanoTime();
                for (int i = 0; i < thread_array[k]; i++)
                {
                    t[i] = new Thread(new Main());
                    t[i].start();
                }
    
                for (int i = 0; i < thread_array[k]; i++)
                    t[i].join();
    
                long end = System.nanoTime();
                total += end - start;
    
                // Reset everything for next round
                q = new UnboundedDequeue<>();
                counter = new AtomicInteger(0);
            }

            total /= 1000;
            System.out.println("Average time to for " + thread_array[k] + " threads to execute " + CALLS + " calls: " + (total) + "ns");
            System.out.println("operations per 1 second for " + thread_array[k] + " threads: " + (CALLS * 1e9)/total);
            System.out.println("=====================================");
            System.out.println();
        }

        return;
    }
}
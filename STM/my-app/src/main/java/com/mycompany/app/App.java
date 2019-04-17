package com.mycompany.app;

import java.util.concurrent.atomic.AtomicInteger;

import org.multiverse.api.StmUtils;

public class App extends Thread
{
    public static STMUnboundedDeque<Integer> q = new STMUnboundedDeque<>();
    public static AtomicInteger counter = new AtomicInteger(0);
    public static final int CALLS = 10000, THREADS = 7;
    public static int [] pusharray = new int[CALLS];
    public static int [] poparray = new int[CALLS];

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

        // for (int i = counter.getAndIncrement(); i < CALLS; i = counter.getAndIncrement())
		// {
        //     double r2 = Math.random();
        //     pusharray[i]++;
        //     if (r2 >= 0.50)
        //         q.push_left(i);
        //     else
        //         q.push_right(i);   
        // }

        // System.out.println("Thread #" + currentThread().getId() + " is sleeping...");
        // try{
        //     Thread.sleep(1000);
        // } catch (Exception e)
        // {
        //     ;
        // }
        // System.out.println("Thread #" + currentThread().getId() + " WOKE UP!!!");
        

        // for (int i = counter.getAndDecrement(); i > 0; i = counter.getAndDecrement())
        // {
        //     double r2 = Math.random();

        //     if (r2 >= 0.50)
        //     {
        //         Integer j = q.pop_left();
        //         if (j != null)
        //             pusharray[j]++;
        //     }
        //     else
        //     {
        //         Integer j = q.pop_right();
        //         if (j != null)
        //             pusharray[j]++;
        //     }
        // }
    }

    public static void main(String [] args) throws Exception
    {
        Thread [] t = new Thread[THREADS];

        // To calculate the average
        long total = 0;

        for (int j = 0; j < 1000; j++)
		{
			long start = System.nanoTime();
			for (int i = 0; i < THREADS; i++)
			{
				t[i] = new Thread(new App());
				t[i].start();
			}

			for (int i = 0; i < THREADS; i++)
				t[i].join();

			long end = System.nanoTime();
			total += end - start;

			// Reset everything for next round
			q = new STMUnboundedDeque<>();
			counter = new AtomicInteger(0);
        }

        System.out.println("Average time to for " + THREADS + " threads to execute " + CALLS + " calls: " + (total / 1000) + "ns");
        return;
    }
}
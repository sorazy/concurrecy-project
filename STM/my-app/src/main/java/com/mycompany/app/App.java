package com.mycompany.app;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.multiverse.api.StmUtils;

public class App extends Thread
{
    public static UnboundedDequeue<Integer> q = new UnboundedDequeue<>();
    public static AtomicInteger counter = new AtomicInteger(0);
    public static final int CALLS = 10000, THREADS = 5;
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
                push_pop_left(i);
            }
            else try
            {
                push_pop_right(i);
            }
            catch(Exception e)
            {
                ;
            }
        }
    }

    public void push_pop_left(final int data)
    {
        StmUtils.atomic(new Runnable() {
            @Override
            public void run()
            {
                q.push_left(data);
                q.pop_left();
            }
        });
    }

    public void push_pop_right(final int data)
    {
        StmUtils.atomic(new Runnable() {
            @Override
            public void run()
            {
                q.push_left(data);
                q.pop_left();
            }
        });
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
			q = new UnboundedDequeue<>();
			counter = new AtomicInteger(0);
        }
        
        System.out.println("Average time to for " + THREADS + " threads to execute " + CALLS + " calls: " + (total / 1000) + "ns");
        return;
    }
}
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class EliminationArray<T>
{
    LockFreeExchanger<T> [] exchanger;
    AtomicInteger matchCount;

    EliminationArray(int capacity)
    {
        exchanger = new LockFreeExchanger[capacity];
        
        for (int i = 0; i < capacity; i++)
            exchanger[i] = new LockFreeExchanger<>();

        matchCount = new AtomicInteger(0);
    }

    public T visit(T value, int range, int duration) throws Exception
    {
        int slot = (int)(Math.random() * range);
        T old = value;

        T newT = exchanger[slot].exchange(value, duration);

        if (old != newT)
            matchCount.incrementAndGet();

        return newT;
    }
}
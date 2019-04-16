import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class EliminationArray<T>
{
    LockFreeExchanger<T> [] exchanger;

    EliminationArray(int capacity)
    {
        exchanger = new LockFreeExchanger[capacity];
        
        for (int i = 0; i < capacity; i++)
            exchanger[i] = new LockFreeExchanger<>();
    }

    public T visit(T value, int range, int duration) throws Exception
    {
        int slot = (int)(Math.random() * range);
        return exchanger[slot].exchange(value, duration);
    }
}
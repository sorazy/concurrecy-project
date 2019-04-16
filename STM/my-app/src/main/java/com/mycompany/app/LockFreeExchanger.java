import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeExchanger<T>
{
    public static final int EMPTY = -1, WAITING = 0, DONE = 1;
    AtomicStampedReference<T> slot = new AtomicStampedReference<T>(null, 0);
    
    public T exchange(T myItem, long timeout) throws Exception
    {
        long timeBound = System.nanoTime() + timeout;
        int [] stampHolder = {EMPTY};

        while (true)
        {
            if (System.nanoTime() > timeBound)
                throw new Exception();

            T yrItem = slot.get(stampHolder);

            switch(stampHolder[0])
            {
            case EMPTY:
                if (slot.compareAndSet(yrItem, myItem, EMPTY, WAITING))
                {
                    while (System.nanoTime() < timeBound)
                    {
                        yrItem = slot.get(stampHolder);
                        if (stampHolder[0] == DONE)
                        {
                            slot.set(null, EMPTY);
                            return yrItem;
                        }
                    }

                    if (slot.compareAndSet(myItem, null, WAITING, EMPTY))
                    {
                        throw new Exception();
                    }
                    else
                    {
                        yrItem = slot.get(stampHolder);
                        slot.set(null, EMPTY);
                        return yrItem;
                    }
                }
                break;
            case WAITING:
                if (slot.compareAndSet(yrItem, myItem, WAITING, DONE))
                    return yrItem;
                break;
            case DONE:
                break;
            }                
        }
    }
}
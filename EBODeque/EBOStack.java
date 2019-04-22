// Soliman Alnaizy - An implementation of an Elimination Backoff Array
// Spring 2019

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EBOStack<T>
{
    AtomicReference<Node<T>> top = new AtomicReference<>(null);
    public static final int MIN_DELAY = 1000, MAX_DELAY = (int) 100000;
    public static final int CAPACITY = 100;    
    EliminationArray<T> array = new EliminationArray<>(CAPACITY);  
    
    // Since the EA could potentially hold up to 10,000 items, we don't want to search the entire array
    // everytime. So we first start with a small range, and then expand as need be.
    static ThreadLocal<RangePolicy> policy = new ThreadLocal<RangePolicy>()
    {
        protected synchronized RangePolicy initialValue() {
            return new RangePolicy(CAPACITY);
        }
    };

    public void push(T value)
    {
        RangePolicy range = policy.get();
        Node<T> newNode = new Node<>(value);
        int limit = MIN_DELAY;

        while (true)
        {
            Node<T> oldTop = top.get();
            newNode.next = oldTop;

            // delay tells us long are we going to search the EA for
            int delay = (int)(Math.random() * limit);

            // Starts off by a typical CAS with the current top
            if (top.compareAndSet(oldTop, newNode)) return;

            // If the CAS failed, check the EA for any threads that are popping
            else try
            {
                // If the EA returns null, that means we found a pop() pair. Return.
                T otherValue = array.visit(value, range.getRange(), delay);
                if (otherValue == null)
                {
                    range.recordEliminationSuccess();
                    return; // Was able to find a pop() pair :D
                }
            }
            catch (Exception e)
            {                
                // If we cought an Exception, that means a pop() wasn't found.
                // Increase the limit and try again :(
                limit = Math.min(MAX_DELAY, 2 * delay);
                range.recordEliminationTimeout();
            }
        }
    }

    // Similar logic to the push() method. We try to push onto the stack using a CAS method.
    // If the CAS fails, we try to look at the elimination stack.
    public T pop() throws Exception
    {
        RangePolicy range = policy.get();
        int limit = MIN_DELAY;

        while (true)
        {
            Node<T> oldTop = top.get();

            if (oldTop == null) throw new EmptyStackException();
            else if (top.compareAndSet(oldTop, oldTop.next)) return oldTop.data;
            else try
            {
                int delay = (int)(Math.random() * limit);
                T otherValue = array.visit(null, range.getRange(), delay);
                if (otherValue != null)
                {
                    range.recordEliminationSuccess();
                    return otherValue; // Was able to find a push() pair :D
                }

                limit = Math.min(MAX_DELAY, 2 * delay);
            }
            catch (Exception e)
            {
                range.recordEliminationTimeout();    
            }
        }
    }
}
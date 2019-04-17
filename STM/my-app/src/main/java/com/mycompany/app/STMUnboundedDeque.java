package com.mycompany.app;

import org.multiverse.api.StmUtils;
import org.multiverse.api.Txn;
import org.multiverse.api.callables.TxnCallable;
import org.multiverse.api.references.TxnInteger;
import org.multiverse.api.references.TxnRef;

class Node<T>
{
    T data;
    Node<T> next, prev;

    Node(T data)
    {
        this.data = data;
    }
}

public class STMUnboundedDeque<T>
{
    private final TxnRef<Node<T>> head, tail;
    private final TxnInteger size;
    
    public STMUnboundedDeque()
    {
        this.head = StmUtils.newTxnRef(null);
        this.tail = StmUtils.newTxnRef(null);
        this.size = StmUtils.newTxnInteger(0);
    }

    public void push_left(final T data)
    {
        StmUtils.atomic(new Runnable() {

            @Override
            public void run() {
                Node<T> newNode = new Node<>(data);
                Node<T> oldHead = (Node<T>) head.get();
                newNode.next = oldHead;
                newNode.prev = null;
                if (oldHead != null) oldHead.prev = newNode;
                head.set(newNode);      
                size.increment();
            }
        });
    }

    public void push_right(final T data)
    {
        StmUtils.atomic(new Runnable() {

            @Override
            public void run() {
                Node<T> newNode = new Node<>(data);
                Node<T> oldTail = (Node<T>) tail.get();
                newNode.prev = oldTail;
                newNode.next = null;
                if (oldTail != null) oldTail.next = newNode;
                tail.set(newNode);
                size.increment();
            }
        });
    }

    public T pop_left()
    {
        return StmUtils.atomic(new TxnCallable<T>() {
          @Override
          public T call(final Txn txn) throws Exception {
            Node<T> oldHead = (Node<T>) head.get();

            if (oldHead == null) return null;

            T retVal = oldHead.data;

            oldHead.prev = null;
            head.set(oldHead.next);
            size.decrement();
            return retVal;
          }
        });
    }

    public T pop_right()
    {
        return StmUtils.atomic(new TxnCallable<T>() {
          @Override
          public T call(final Txn txn) throws Exception {
              Node<T> oldTail = (Node<T>) tail.get();

              if (oldTail == null) return null;

              T retVal = oldTail.data;

              oldTail.next = null;
              tail.set(oldTail.prev);
              size.decrement();
              return retVal;
          }
        });
    }

    public int size()
    {
        return StmUtils.atomic(new TxnCallable<Integer>() {
          
          @Override
          public Integer call(final Txn txn) throws Exception {
              return size.get();
          }
        });
    }
}
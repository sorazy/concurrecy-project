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
        Node<T> h = new Node<>(null);
        Node<T> t = new Node<>(null);
        this.head = StmUtils.newTxnRef(h);
        this.tail = StmUtils.newTxnRef(t);
        this.size = StmUtils.newTxnInteger(0);
    }

    public void push_left(final T data)
    {
        for (int i = 0; i < 5000; i++);

        StmUtils.atomic(new Runnable() {

            @Override
            public void run() {
                Node<T> newNode = new Node<>(data);
                Node<T> oldHead = (Node<T>) head.get();
                newNode.next = oldHead;
                head.set(newNode);      
            }
        });
    }

    public void push_right(final T data)
    {
        for (int i = 0; i < 5000; i++);

        StmUtils.atomic(new Runnable() {

            @Override
            public void run() {
                Node<T> newNode = new Node<>(data);
                Node<T> oldTail = (Node<T>) tail.get();
                newNode.prev = oldTail;
                tail.set(newNode);
            }
        });
    }

    public T pop_left()
    {
        for (int i = 0; i < 5000; i++);

        return StmUtils.atomic(new TxnCallable<T>() {
          @Override
          public T call(final Txn txn) throws Exception {
            Node<T> oldHead = (Node<T>) head.get();

            if (oldHead.data == null) return null;

            head.set(oldHead.next);
            return oldHead.data;
          }
        });
    }

    public T pop_right()
    {
        for (int i = 0; i < 5000; i++);

        return StmUtils.atomic(new TxnCallable<T>() {
          @Override
          public T call(final Txn txn) throws Exception {
              Node<T> oldTail = (Node<T>) tail.get();

              if (oldTail.data == null) return null;

              tail.set(oldTail.prev);
              return oldTail.data;
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
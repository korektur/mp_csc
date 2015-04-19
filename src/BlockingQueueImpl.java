import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueImpl<T> {

    private class Node {
        Node next;
        Node prev;
        T value;
    }

    private volatile int size;
    private Node head;
    private Node tail;
    private final Lock lock;
    private final int CAPACITY;

    public BlockingQueueImpl(int capacity) {
        size = 0;
        head = new Node();
        tail = head;
        lock = new ReentrantLock();
        CAPACITY = capacity;
    }

    public void push(@NotNull T value) throws InterruptedException {
        Node node = new Node();

        lock.lock();

        while (size >= CAPACITY)
            lock.wait();

        tail.next = node;
        node.prev = tail;
        tail = node;
        ++size;
        lock.unlock();

        node.value = value;
    }

    public T pop() throws InterruptedException {
        lock.lock();
        while(size == 0)
            lock.wait();
        final T value = head.value;
        head = head.next;
        --size;
        lock.unlock();
        lock.notifyAll();
        return value;
    }
}

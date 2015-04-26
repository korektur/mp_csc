import org.jetbrains.annotations.NotNull;

public class BlockingQueueImpl<T> {

    private class Node {
        Node next;
        Node prev;
        T value;
    }

    private volatile int size;
    private Node head;
    private Node tail;
    private final int CAPACITY;

    public BlockingQueueImpl(int capacity) {
        size = 0;
        head = new Node();
        tail = head;
        CAPACITY = capacity;
    }

    public synchronized void push(@NotNull T value) throws InterruptedException {
        Node node = new Node();

        while (size >= CAPACITY)
            this.wait();

        tail.next = node;
        node.prev = tail;
        tail = node;
        ++size;
        node.value = value;
        this.notifyAll();
    }

    public synchronized T pop() throws InterruptedException {
        while (size == 0)
            this.wait();
        final T value = head.value;
        head = head.next;
        --size;
        this.notifyAll();
        return value;
    }

    public int size() {
        return size;
    }
}

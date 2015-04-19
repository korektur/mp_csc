import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class FutureImpl<T> implements Future<T>, Runnable {

    private final Runnable task;
    private final T result;
    private final AtomicReference<States> state;
    private volatile Thread runningThread;

    private enum States {
        NEW,
        IN_PROCESS,
        DONE,
        CANCELED,
    }

    public FutureImpl(Runnable task, T result) {
        this.task = task;
        this.result = result;
        state = new AtomicReference<>(States.NEW);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (state.get() == States.CANCELED) return true;
        if (state.compareAndSet(States.NEW, States.CANCELED)) {
            return true;
        } else if (mayInterruptIfRunning) {
            //noinspection StatementWithEmptyBody
            while(runningThread == null) ;
            try {
                runningThread.interrupt();
            } finally {
                state.set(States.CANCELED);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return state.get() == States.CANCELED;
    }

    @Override
    public boolean isDone() {
        return state.get() == States.DONE;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        while (state.get() != States.DONE || state.get() != States.CANCELED) {
            synchronized (task) {
                task.wait();
            }
        }
        if (state.get() == States.CANCELED) throw new CancellationException();
        return result;
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long deadline = unit.toMillis(timeout) + System.currentTimeMillis();
        while (state.get() != States.DONE || state.get() != States.CANCELED) {
            synchronized (task) {
                task.wait(unit.toMillis(timeout));
            }
            if (System.currentTimeMillis() >= deadline) break;
        }
        if (state.get() == States.CANCELED) throw new CancellationException();
        if (state.get() != States.DONE) throw new TimeoutException();
        return result;
    }

    @Override
    public void run() {
        if (state.compareAndSet(States.NEW, States.IN_PROCESS)) {
            runningThread = Thread.currentThread();
            task.run();
            state.set(States.DONE);
            synchronized (task) {
                task.notifyAll();
            }
        }
    }
}

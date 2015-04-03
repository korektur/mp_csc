import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class FutureImpl<T> implements Future<T>, Runnable {

    private final Runnable task;
    private final T result;
    private AtomicReference<States> state;
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
        if (state.compareAndSet(States.NEW, States.CANCELED)) {
            return true;
        } else if (mayInterruptIfRunning) {
            while(runningThread == null) {
                //TODO
            }
            runningThread.interrupt();
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
        while (state.get() != States.DONE) {
            task.wait();
        }
        return result;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null)
            throw new NullPointerException();
        if (state.get() != States.DONE) {
            task.wait(unit.toMillis(timeout));
        }
        if (state.get() != States.DONE) return null;
        return result;
    }

    @Override
    public void run() {
        if (state.compareAndSet(States.NEW, States.IN_PROCESS)) {
            runningThread = Thread.currentThread();
            task.run();
            state.set(States.DONE);
            task.notifyAll();
        }
    }
}

import java.util.ArrayList;

public class ThreadPool {

    ArrayList<Thread> threads;

    public ThreadPool(int nThreads) {
        threads = new ArrayList<>(nThreads);
        for (int i = 0; i < nThreads; ++i) {
            threads.add(new Thread());
        }
    }
}

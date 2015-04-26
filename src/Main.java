import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ruslan Akhundov
 */
public class Main {

    public static void main(String[] args) {
        ThreadPoolImpl threadPool = new ThreadPoolImpl(10);
        TaskFactory taskFactory = new TaskFactory();


        threadPool.execute();
        ArrayList<Future<?>> results = new ArrayList<>(100);
        for (int i = 0; i < 1; ++i) {
            try {
                results.add(threadPool.submit(Executors.callable(taskFactory.createTask(i))));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 1; ++i) {
            try {
                results.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}

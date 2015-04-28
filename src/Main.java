import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Ruslan Akhundov
 */
public class Main {

    private static void printUsage() {
        System.out.println("Type \"submit [DURATION]\" to add task with specified DURATION in milliseconds.");
        System.out.println("Type \"cancel [id]\" to cancel task with specified id.");
        System.out.println("Type \"status [id]\" to get status of task with specified id.");
        System.out.println("Type \"exit\" to exit");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Number of threads: ");
        ThreadPoolImpl threadPool = new ThreadPoolImpl(Integer.parseInt(in.readLine()));
        threadPool.execute();

        while(true) {
            printUsage();
            String input = in.readLine();
//            if (input.startsWith("submit "))
            if ("exit".equals(input)) {
                threadPool.interrupt();
                return;
            }
        }
    }
}

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private static final String ACTIVEMQ_LOCATION = "tcp://172.17.0.20:61616";

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new Producer(ACTIVEMQ_LOCATION));
        executor.submit(new Consumer(ACTIVEMQ_LOCATION));
        executor.shutdown();
    }
}
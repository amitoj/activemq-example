import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Producer implements Runnable {

    private String ACTIVEMQ_LOCATION;

    public Producer(String ACTIVEMQ_LOCATION) {
        this.ACTIVEMQ_LOCATION = ACTIVEMQ_LOCATION;
    }

    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_LOCATION);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            long now = System.currentTimeMillis();
            long DELAY = 30 * 1000;

            for (int outerloop = 0; outerloop < 100; outerloop++) {
                long deliverAfterPeriod = outerloop * DELAY;

                for (int i = 0; i < 100; i++) {
                    String text = "Message id: " + outerloop + "-" + i + " @ " + System.currentTimeMillis();
                    TextMessage message = session.createTextMessage(text);

                    message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, deliverAfterPeriod);

                    producer.send(message);
                    System.out.println("Sent message: " + text);
                }
            }

            // TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(250, 500));

            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        System.out.println(args[0]);
        executor.submit(new Producer(args[0]));
        executor.shutdown();
    }

}



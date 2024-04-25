///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.rabbitmq.jms:rabbitmq-jms:3.2.0
//DEPS org.slf4j:slf4j-simple:1.7.36

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import jakarta.jms.*;

import java.util.Date;

public class Sender {

  static String TOPIC_NAME = "vesc-1103";

  public static void main(String[] args) throws Exception {
    RMQConnectionFactory cf = new RMQConnectionFactory();
    int port = args.length == 1 ? Integer.parseInt(args[0]) : 5672;
    System.out.println("Connecting to port " + port);
    cf.setPort(port);
    TopicConnection c = cf.createTopicConnection();
    c.start();
    TopicSession topicSession = c.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
    Topic topic = topicSession.createTopic(TOPIC_NAME);
    TopicPublisher sender = topicSession.createPublisher(topic);
    sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    System.out.println("Sending messages...");
    int count = 0;
    while (true) {
      try {
        TextMessage message = topicSession.createTextMessage("sent at " + new Date());
        message.setBooleanProperty("boolProp", true);
        sender.send(message);
        count++;
        if (count % 10 == 0) {
          System.out.println(count + " messages sent");
        }
      } catch (Exception e) {
        System.out.println("Error while sending " + e.getMessage());
      }

      Thread.sleep(500);
    }

  }

}

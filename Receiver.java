///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.rabbitmq.jms:rabbitmq-jms:3.2.0
//DEPS org.slf4j:slf4j-simple:1.7.36

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import jakarta.jms.*;

public class Receiver {

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
    TopicSubscriber subscriber = topicSession.createSubscriber(topic, "boolProp", false);

    while (true) {
      try {
        TextMessage message = (TextMessage) subscriber.receive(1000);
        if (message != null) {
          System.out.println("received with body: " + message.getText());
        }
      } catch (Exception e) {
        System.out.println("Error while receiving " + e.getMessage());
        Thread.sleep(500);
      }
    }
  }

}

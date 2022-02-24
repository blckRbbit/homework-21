package com.blck_rbbt.consoleRabbitMQHomework.consumer;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ArticleConsumer {
    private static final String EXCHANGE_NAME = "articles_channel";
    private static final String QUEUE_NAME = "programming_languages";

    public static void main(String[] args) {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connect() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String[] arr = handle();

        if (arr[0].equals("set_topic")) {
            String theme = arr[1];
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, theme);
            System.out.println("[*] Waiting for messages...");

            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(Thread.currentThread().getName());
                System.out.println(" [x] Received -> " + delivery.getEnvelope().getRoutingKey() + ": " + message);
            };

            channel.basicConsume(QUEUE_NAME, true, callback, consumerTag -> {});
        }

        if (arr[0].equals("unsubscribe")) {
            String theme = arr[1];
            channel.queueDelete(theme);
            System.out.println(String.format("Topic <%s> is unsubscribed", theme));

            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(Thread.currentThread().getName());
                System.out.println(" [x] Received -> " + delivery.getEnvelope().getRoutingKey() + ": " + message);
            };

            channel.basicConsume(QUEUE_NAME, true, callback, consumerTag -> {});
        }

    }

    private static String[] handle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Subscribe: ");
        String in = scanner.nextLine().trim();
        String[] arr;
        if (in.contains(" ") && in.toLowerCase().startsWith("set_topic")
                || in.contains(" ") && in.toLowerCase().startsWith("unsubscribe")) {
            arr = in.split(" ", 2);
        } else {
            arr = new String[] {"error command", "error channel"};
        }
        return arr;
    }
}

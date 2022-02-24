package com.blck_rbbt.consoleRabbitMQHomework.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ArticleProducer {
    private static final String EXCHANGE_NAME = "articles_channel";
    private static final String QUEUE_NAME = "programming_languages";

    public static void main(String[] argv) throws Exception {
        connect();
    }

    private static void connect() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("OK");

        String[] arr = processTheMessage();
        String theme = arr[0];
        String message = arr[1];
        channel.basicPublish(EXCHANGE_NAME, theme, null, message.getBytes(StandardCharsets.UTF_8));
    }

    private static String[] processTheMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input message: ");
        String in = scanner.nextLine().trim();
        String[] arr;
        if (in.contains(" ")) {
            arr = in.split(" ", 2);
        } else {
            arr = new String[] {"error theme", "error message"};
        }
        return arr;
    }

}
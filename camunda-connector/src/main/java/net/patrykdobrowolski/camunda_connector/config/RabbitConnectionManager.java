package net.patrykdobrowolski.camunda_connector.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Log
public class RabbitConnectionManager {

    private static volatile Connection connection;
    private static final Object LOCK = new Object();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(RabbitConnectionManager::closeConnection));
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            synchronized (LOCK) {
                openConnectionIfNotOpen();
            }
        }
        return connection;
    }

    private static void openConnectionIfNotOpen() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            RabbitConfiguration rabbitConfiguration = RabbitConfiguration.getInstance();
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitConfiguration.getHost());
            factory.setPort(rabbitConfiguration.getPort());
            factory.setUsername(rabbitConfiguration.getUsername());
            factory.setPassword(rabbitConfiguration.getPassword());
            factory.setVirtualHost(rabbitConfiguration.getVirtualHost());
            connection = factory.newConnection();
        }
    }

    private static void closeConnection() {
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
                log.info("RabbitMQ connection closed successfully.");
            } catch (Exception e) {
                log.severe("RabbitMQ connection close failed.");
            }
        }
    }
}

package br.com.ecommerce.orders.testcontainers;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

public class RabbitMQTestContainer implements BeforeAllCallback {

    @Container
    private static RabbitMQContainer rabbit;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

            rabbit = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
                .withExposedPorts(5672, 15672);
            rabbit.start();

            Integer mappedPort = rabbit.getMappedPort(5672);
            System.setProperty("spring.rabbitmq.port", mappedPort.toString());
    }
}
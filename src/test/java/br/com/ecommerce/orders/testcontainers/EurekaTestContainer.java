package br.com.ecommerce.orders.testcontainers;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class EurekaTestContainer implements BeforeAllCallback {

    private static GenericContainer rabbit;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

            rabbit = new GenericContainer("mateuspontessan/eureka-server")
                .withExposedPorts(9091);
            rabbit.start();

            Integer mappedPort = rabbit.getMappedPort(9091);
            System.setProperty("eureka.client.serviceUrl.defaultZone", "//localhost:" + mappedPort.toString() +"/eureka");
    }
}
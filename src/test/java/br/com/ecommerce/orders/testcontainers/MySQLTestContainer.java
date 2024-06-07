package br.com.ecommerce.orders.testcontainers;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class MySQLTestContainer implements BeforeAllCallback {

    private static GenericContainer<?> mysql;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
            mysql = new GenericContainer("mysql:8.0.36")
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD", "root");
            mysql.start();

            Integer mappedPort = mysql.getMappedPort(3306);
            System.setProperty("spring.datasource.url", "jdbc:mysql://localhost:" + mappedPort + "/" + context.getDisplayName() + "?createDatabaseIfNotExist=true");
    }
}
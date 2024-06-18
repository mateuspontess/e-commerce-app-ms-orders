package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.ecommerce.orders.testcontainers.MySQLTestContainerConfig;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MySQLTestContainerConfig.class)
class OrdersApplicationTests {

	@Container
	private static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
		.withExposedPorts(5672, 15672);

	@DynamicPropertySource
	static void configure(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.port", () -> rabbit.getAmqpPort());
		registry.add("eureka.client.enabled", () -> false);
	}

	@Test
	void contextLoads() {
	}
}
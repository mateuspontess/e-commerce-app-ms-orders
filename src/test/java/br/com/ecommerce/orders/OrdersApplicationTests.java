package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.orders.testcontainers.MySQLTestContainerConfig;
import br.com.ecommerce.orders.testcontainers.RabbitMQTestContainerConfig;

@SpringBootTest
@TestPropertySource(properties = "classpath:application-test.properties")
@Import({MySQLTestContainerConfig.class, RabbitMQTestContainerConfig.class})
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}
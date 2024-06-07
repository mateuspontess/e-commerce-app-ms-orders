package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.orders.testcontainers.EurekaTestContainer;
import br.com.ecommerce.orders.testcontainers.MySQLTestContainer;
import br.com.ecommerce.orders.testcontainers.RabbitMQTestContainer;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({EurekaTestContainer.class, RabbitMQTestContainer.class, MySQLTestContainer.class})
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}
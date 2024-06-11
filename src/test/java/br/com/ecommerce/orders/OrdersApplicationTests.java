package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.orders.testcontainers.RabbitMQTestContainer;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
@ExtendWith(RabbitMQTestContainer.class)
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}
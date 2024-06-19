package br.com.ecommerce.orders.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.orders.model.PaymentDTO;
import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.model.order.OrderCreateDTO;
import br.com.ecommerce.orders.model.order.OrderDTO;
import br.com.ecommerce.orders.model.order.OrderStatus;
import br.com.ecommerce.orders.model.product.StockWriteOffDTO;
import br.com.ecommerce.orders.service.OrderService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService service;
	@Autowired
	private RabbitTemplate template;
	
	
	@PostMapping
	@Transactional
	public ResponseEntity<OrderDTO> createOrder(
		@RequestBody @Valid OrderCreateDTO dto, 
		@RequestHeader("X-auth-user-id") Long userId,
		UriComponentsBuilder uriBuilder
		) {
		Order order = service.saveOrder(dto, userId);
		
		PaymentDTO paymentCreateRabbit = new PaymentDTO(
				order.getId(), order.getUserId(), order.getTotal());
		List<StockWriteOffDTO> stockUpdateRabbit = order.getProducts().stream()
				.map(o -> new StockWriteOffDTO(o.getProductId(), o.getUnit() * -1))
				.toList();
		OrderDTO responseBody = new OrderDTO(order);
		
		var uri = uriBuilder.path("/orders/{orderId}").buildAndExpand(responseBody.id()).toUri();
		template.convertAndSend("orders.create.ex", "payment", paymentCreateRabbit);
		template.convertAndSend("orders.create.ex", "stock", stockUpdateRabbit);
		return ResponseEntity.created(uri).body(responseBody);
	}
	
	@GetMapping
	public ResponseEntity<Page<OrderBasicInfDTO>> getAllBasicsInfoOrdersByUser(
		@PageableDefault(size = 10) Pageable pageable,
		@RequestHeader("X-auth-user-id") Long userId
		) {
		
		return ResponseEntity.ok(service.getAllOrdersByUser(pageable, userId));
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByIdAndUserId(
		@PageableDefault(size = 10) Pageable pageable,
		@PathVariable Long orderId,
		@RequestHeader("X-auth-user-id") Long userId
		) {
		
		return ResponseEntity.ok(service.getOrderById(orderId, userId));
	}
	
	@PatchMapping("/{orderId}")
	@Transactional
	public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, @RequestHeader("X-auth-user-id") String token) {
		service.updateOrderStatus(orderId, OrderStatus.CANCELED);
		
		// cancel payment
		template.convertAndSend("orders-cancel.ex", "cancellation", orderId);
		return ResponseEntity.noContent().build();
	}
}
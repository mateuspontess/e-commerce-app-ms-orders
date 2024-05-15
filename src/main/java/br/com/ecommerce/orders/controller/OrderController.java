package br.com.ecommerce.orders.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<?> create(
			@RequestBody @Valid OrderCreateDTO dto, 
			@RequestHeader("X-auth-user-id") Long userId
			) {
		Order order = service.saveOrder(dto, userId);
		
		PaymentDTO paymentCreate = new PaymentDTO(
				order.getId(), order.getUserId(), order.getTotal());
		
		List<StockWriteOffDTO> stockUpdate = order.getProducts().stream()
				.map(o -> new StockWriteOffDTO(o.getProductId(), o.getUnit()))
				.toList();
		
		template.convertAndSend("order.create.ex", "payment", paymentCreate);
		template.convertAndSend("order.create.ex", "stock", stockUpdate);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ResponseEntity<?> getAllBasicsInfoOrdersByUser(
			@PageableDefault(size = 10) Pageable pageable,
			@RequestHeader("X-auth-user-id") Long userId
			) {
		
		Page<OrderBasicInfDTO> orders = service.getAllOrdersByUser(pageable, userId);
		return ResponseEntity.ok(orders);
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<?> getOrderByIdAndUserId(
			@PageableDefault(size = 10) Pageable pageable,
			@PathVariable Long orderId,
			@RequestHeader("X-auth-user-id") Long userId
			) {
		
		OrderDTO orders = service.getOrderById(orderId, userId);
		return ResponseEntity.ok(orders);
	}
	
	@PutMapping("/{orderId}")
	@Transactional
	public ResponseEntity<?> cancelOrder(
			@PathVariable Long orderId,
			@RequestHeader("X-auth-user-id") String token
			) {
		
		service.updateOrderStatus(orderId, OrderStatus.CANCELED);
		
		template.convertAndSend("order-cancel.ex", "cancellation", orderId);
		return ResponseEntity.ok().build();
	}
}
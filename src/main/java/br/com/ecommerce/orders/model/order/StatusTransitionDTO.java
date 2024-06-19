package br.com.ecommerce.orders.model.order;

public record StatusTransitionDTO(
		
	Long orderId, 
	OrderStatus status
) {}
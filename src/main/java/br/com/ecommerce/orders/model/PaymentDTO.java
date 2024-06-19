package br.com.ecommerce.orders.model;

import java.math.BigDecimal;

public record PaymentDTO(
		
	Long orderId,
	Long userId,
	BigDecimal paymentAmount
) {}
package br.com.ecommerce.orders.model.product;

import java.math.BigDecimal;

public record ProductAndPriceDTO(Long id, BigDecimal price) {}
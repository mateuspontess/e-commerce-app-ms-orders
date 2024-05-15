package br.com.ecommerce.orders.model.order;

import java.util.List;

import br.com.ecommerce.orders.model.product.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderCreateDTO(@NotNull @Valid List<ProductDTO> products) {}
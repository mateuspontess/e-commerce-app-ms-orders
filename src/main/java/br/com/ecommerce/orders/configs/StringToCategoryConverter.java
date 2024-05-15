package br.com.ecommerce.orders.configs;

import org.springframework.core.convert.converter.Converter;

import br.com.ecommerce.orders.model.order.OrderStatus;

public class StringToCategoryConverter implements Converter<String, OrderStatus>{

	@Override
	public OrderStatus convert(String source) {
		try {
			return OrderStatus.fromString(source);
		
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
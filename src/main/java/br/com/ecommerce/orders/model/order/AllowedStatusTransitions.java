package br.com.ecommerce.orders.model.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllowedStatusTransitions {

	private static final Map<OrderStatus, List<OrderStatus>> map = new HashMap<OrderStatus, List<OrderStatus>>();
	
	static {
		map.put(OrderStatus.AWAITING_PAYMENT, Arrays.asList(OrderStatus.CONFIRMED_PAYMENT, OrderStatus.CANCELED));
		map.put(OrderStatus.CONFIRMED_PAYMENT, Arrays.asList(OrderStatus.IN_TRANSIT, OrderStatus.CANCELED));
		map.put(OrderStatus.IN_TRANSIT, Arrays.asList(OrderStatus.DELIVERED));
	}
	
	public static List<OrderStatus> getAllowedOrderStatus(OrderStatus status) {
		return map.getOrDefault(status, null);
	}
}
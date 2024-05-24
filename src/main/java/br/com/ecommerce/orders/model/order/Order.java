package br.com.ecommerce.orders.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.orders.model.product.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity(name = "Order")
@Table(name = "orders")
public class Order {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<Product>();
	
	private BigDecimal total;
	
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	
	public Order(Long userId, List<Product> products, BigDecimal total, OrderStatus status) {
		this.products = products;
		this.userId = userId;
		this.status = status;
		this.total = total;
		this.date = LocalDate.now();
	}
	
	public void updateOrderStatus(OrderStatus newStatus) {
		List<OrderStatus> allowedStatuses = AllowedStatusTransitions.getAllowedOrderStatus(this.status);
		if(allowedStatuses == null || !allowedStatuses.contains(newStatus)) 
			throw new IllegalArgumentException("The status " + this.status + " cannot transition to " + newStatus);
		
		this.status = newStatus;
	}
	
	@Override
	public String toString() {
		return String.format("Order(userId=%d, total=%s, status=%s)", this.userId, this.total.toString(), this.status);
	}
}
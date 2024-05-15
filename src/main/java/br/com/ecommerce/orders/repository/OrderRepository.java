package br.com.ecommerce.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.orders.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
}
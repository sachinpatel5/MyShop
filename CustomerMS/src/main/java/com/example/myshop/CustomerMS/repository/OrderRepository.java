package com.example.myshop.CustomerMS.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.CustomerMS.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Integer> {
	List<Order> findByCustomerEmailId(String customerEmailId);
}

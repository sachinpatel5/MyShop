package com.example.myshop.CartMS.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.CartMS.entity.Cart;

public interface CartRepository extends CrudRepository<Cart, Integer>{
	Optional<Cart> findByCustomerEmailId(String customerEmailId);
}

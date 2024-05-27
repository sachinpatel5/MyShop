package com.example.myshop.CartMS.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.CartMS.entity.CartProduct;

public interface CartProductRepository extends CrudRepository<CartProduct, Integer>{

}

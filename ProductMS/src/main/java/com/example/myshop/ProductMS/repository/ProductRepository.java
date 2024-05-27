package com.example.myshop.ProductMS.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.ProductMS.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Integer>{

}

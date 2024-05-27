package com.example.myshop.ProductMS.service;

import java.util.List;

import com.example.myshop.ProductMS.dto.ProductDTO;
import com.example.myshop.ProductMS.exception.MyShopProductException;

public interface ProductService {

	List<ProductDTO> getAllProducts() throws MyShopProductException;

	ProductDTO getProductById(Integer productId) throws MyShopProductException;

	void reduceAvailableQuantity(Integer productId, Integer quantity) throws MyShopProductException;

}

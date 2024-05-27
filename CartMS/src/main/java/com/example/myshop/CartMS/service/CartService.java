package com.example.myshop.CartMS.service;

import java.util.Set;

import com.example.myshop.CartMS.dto.CartDTO;
import com.example.myshop.CartMS.dto.CartProductDTO;
import com.example.myshop.CartMS.exception.MyShopCartException;

public interface CartService {

	public Integer addProductToCart(CartDTO cartDTO) throws MyShopCartException;

	public Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws MyShopCartException;

	public void deleteProductFromCart(String customerEmailId,Integer productId) throws MyShopCartException;

	public void modifyQuantityOfProductInCart(String customerEmailId,Integer productId, Integer quantity) throws MyShopCartException;

	public void deleteAllProductsFromCart(String customerEmailId) throws MyShopCartException;



}

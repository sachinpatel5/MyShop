package com.example.myshop.CartMS.dto;

import jakarta.validation.constraints.PositiveOrZero;

public class CartProductDTO {
	private Integer cartProductId;
	private ProductDTO product;
	@PositiveOrZero
	private Integer quantity;
	public Integer getCartProductId() {
		return cartProductId;
	}
	public void setCartProductId(Integer cartProductId) {
		this.cartProductId = cartProductId;
	}
	public ProductDTO getProduct() {
		return product;
	}
	public void setProduct(ProductDTO product) {
		this.product = product;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}

package com.example.myshop.CartMS.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myshop.CartMS.dto.CartDTO;
import com.example.myshop.CartMS.dto.CartProductDTO;
import com.example.myshop.CartMS.dto.ProductDTO;
import com.example.myshop.CartMS.entity.Cart;
import com.example.myshop.CartMS.entity.CartProduct;
import com.example.myshop.CartMS.exception.MyShopCartException;
import com.example.myshop.CartMS.repository.CartProductRepository;
import com.example.myshop.CartMS.repository.CartRepository;

@Service
public class CartServiceImpl implements CartService{
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartProductRepository cartProductRepository;

	@Override
	public Integer addProductToCart(CartDTO cartDTO) throws MyShopCartException {
		Set<CartProduct> cartProducts=new HashSet<>();
		Integer cartId=null;
		for(CartProductDTO cartProductDTO : cartDTO.getCartProducts()) {
			CartProduct cartProduct =new CartProduct();
			cartProduct.setProductId(cartProductDTO.getProduct().getProductId());
			cartProduct.setQuantity(cartProductDTO.getQuantity());
			cartProducts.add(cartProduct);
		}
		Optional<Cart> cartOptional=cartRepository.findByCustomerEmailId(cartDTO.getCustomerEmailId());
		if(cartOptional.isEmpty()) {
			Cart newCart=new Cart();
			newCart.setCustomerEmailId(cartDTO.getCustomerEmailId());
			newCart.setCartProducts(cartProducts);
			cartRepository.save(newCart);
			cartId=newCart.getCartId();
			}else {
				Cart cart=cartOptional.get();
				for(CartProduct cartProductToBeAdded: cartProducts) {
					Boolean checkProductAlreadyPresent=false;
					for(CartProduct cartProductFromCart: cart.getCartProducts()) {
						if(cartProductFromCart.equals(cartProductToBeAdded)) {
							cartProductFromCart.setQuantity(cartProductToBeAdded.getQuantity()+cartProductFromCart.getQuantity());
							checkProductAlreadyPresent=true;
						}
						if(checkProductAlreadyPresent==false) {
							cart.getCartProducts().add(cartProductToBeAdded);
						}
					}
					cartId=cart.getCartId();
				}
				}
		return cartId;
	}

	@Override
	public Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws MyShopCartException {
		Optional<Cart> cartOptional=cartRepository.findByCustomerEmailId(customerEmailId);
		Set<CartProductDTO> cartProductsDTO=new HashSet<>();
		Cart cart=cartOptional.orElseThrow(()-> new MyShopCartException("CartService.NO_CART_FOUND"));
		if(cart.getCartProducts().isEmpty()) {
			throw new MyShopCartException("CartService.NO_PRODUCT_ADDED_TO_CART");
		}
		Set<CartProduct> cartProducts1 = cart.getCartProducts();
		for(CartProduct cartProduct : cartProducts1) {
			CartProductDTO cartProductDTO = new CartProductDTO();
			cartProductDTO.setCartProductId(cartProduct.getCartProductId());
			cartProductDTO.setQuantity(cartProduct.getQuantity());
			ProductDTO productDTO=new ProductDTO();
			productDTO.setProductId(cartProduct.getProductId());
			cartProductDTO.setProduct(productDTO);
			cartProductsDTO.add(cartProductDTO);		
		}
		
		return cartProductsDTO;
	}

	@Override
	public void deleteProductFromCart(String customerEmailId, Integer productId) throws MyShopCartException {
		Optional<Cart> cartOptional=cartRepository.findByCustomerEmailId(customerEmailId);
		Cart cart=cartOptional.orElseThrow(()->new MyShopCartException("CartService.NO_CART_FOUND"));
		if(cart.getCartProducts().isEmpty()) {
			throw new MyShopCartException("CartService.NO_PRODUCT_ADDED_TO_CART");
		}
		CartProduct selectedProduct=null;
		for(CartProduct product: cart.getCartProducts()) {			
			if(product.getProductId().equals(productId)) {
				selectedProduct=product;
			}
		}
		if(selectedProduct ==null) {
			throw new MyShopCartException("CartService.PRODUCT_ALREADY_NOT_AVAILABLE");
		}
		cart.getCartProducts().remove(selectedProduct);
		cartProductRepository.delete(selectedProduct);
	}

	@Override
	public void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity)
			throws MyShopCartException {
		Optional<Cart> cartOptional=cartRepository.findByCustomerEmailId(customerEmailId);
		Cart cart=cartOptional.orElseThrow(()->new MyShopCartException("CartService.NO_CART_FOUND"));
		if(cart.getCartProducts().isEmpty()) {
			throw new MyShopCartException("CartService.NO_PRODUCT_ADDED_TO_CART");
		}
		CartProduct selectedProduct=null;
		for(CartProduct product: cart.getCartProducts()) {			
			if(product.getProductId().equals(productId)) {
				selectedProduct=product;
			}
		}
		if(selectedProduct ==null) {
			throw new MyShopCartException("CartService.PRODUCT_ALREADY_NOT_AVAILABLE");
		}
		selectedProduct.setQuantity(quantity);
	}

	@Override
	public void deleteAllProductsFromCart(String customerEmailId) throws MyShopCartException {
		Optional<Cart> cartOptional=cartRepository.findByCustomerEmailId(customerEmailId);
		Cart cart=cartOptional.orElseThrow(()->new MyShopCartException("CartService.NO_CART_FOUND"));
		if(cart.getCartProducts().isEmpty()) {
			throw new MyShopCartException("CartService.NO_PRODUCT_ADDED_TO_CART");
		}
		List<Integer> productIds=new ArrayList<>();
		cart.getCartProducts().parallelStream().forEach(cp -> {
			productIds.add(cp.getCartProductId());
			cart.getCartProducts().remove(cp);
		});
		productIds.forEach(pid ->{
			cartProductRepository.deleteById(pid);
		});
		
	}
	
	
	}



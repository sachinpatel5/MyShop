package com.example.myshop.CartMS.api;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;

import com.example.myshop.CartMS.dto.CartDTO;
import com.example.myshop.CartMS.dto.CartProductDTO;
//import com.example.myshop.CartMS.dto.ProductDTO;
import com.example.myshop.CartMS.exception.MyShopCartException;
import com.example.myshop.CartMS.service.CartService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/cart-api")
public class CartAPI {
	@Autowired
	private CartService cartService;
	@Autowired
	private Environment environment;
//	@Autowired
//	private RestTemplate template;

	Log logger = LogFactory.getLog(CartAPI.class);

	@PostMapping("/products")
	ResponseEntity<String> addProductToCart(@RequestBody CartDTO cartDTO) throws MyShopCartException {
		logger.info("Received a request to add products for " + cartDTO.getCustomerEmailId());
		Integer cartId = cartService.addProductToCart(cartDTO);
		String message = environment.getProperty("CartAPI.PRODUCT_ADDED_TO_CART");
		return new ResponseEntity<>(message + " " + cartId, HttpStatus.CREATED);
	}

	@GetMapping(value = "/customer/{customerEmailId}/products")
	public ResponseEntity<Set<CartProductDTO>> getProductsFromCart(
			@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId)
			throws MyShopCartException {
		logger.info("Received Request to get all products from cart of :" + customerEmailId);
		Set<CartProductDTO> cartProductDTOs = cartService.getProductsFromCart(customerEmailId);
//		for (CartProductDTO cartProductDTO : cartProductDTOs) {
//			ProductDTO productDTO = template.getForEntity(
//					"http://ProductMS/" + "/MyShop/product-api/product/" + cartProductDTO.getProduct().getProductId(),
//					ProductDTO.class).getBody();
//
//			cartProductDTO.setProduct(productDTO);
//		}
		return new ResponseEntity<>(cartProductDTOs, HttpStatus.OK);
	}

	@DeleteMapping(value = "/customer/{customerEmailId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(
			@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId,
			@NotNull(message = "{invalid.email.format}") @PathVariable("productId") Integer productId)
			throws MyShopCartException {
		cartService.deleteProductFromCart(customerEmailId, productId);
		return new ResponseEntity<>(environment.getProperty("CartAPI.PRODUCT_DELETED_FROM_CART_SUCCESS"),
				HttpStatus.OK);

	}

	@PutMapping(value = "/customer/{customerEmailId}/product/{productId}")
	public ResponseEntity<String> modifyQuantityOfProductInCart(
			@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId,
			@NotNull(message = "{invalid.email.format}") @PathVariable("productId") Integer productId,
			@RequestBody Integer quantity)
			throws MyShopCartException {
		cartService.modifyQuantityOfProductInCart(customerEmailId,productId,quantity);		
		return new ResponseEntity<>(environment.getProperty("CartAPI.PRODUCT_QUANTITY_UPDATE_FROM_CART_SUCCESS"),HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/customer/{customerEmailId}/products")
	public ResponseEntity<String> deleteAllProductsFromCart(
			@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId)
			throws MyShopCartException {
		cartService.deleteAllProductsFromCart(customerEmailId);
		return new ResponseEntity<>(environment.getProperty("CartAPI.ALL_PRODUCTS_DELETED"),
				HttpStatus.OK);

	}
	
}

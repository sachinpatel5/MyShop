package com.example.myshop.ProductMS.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myshop.ProductMS.dto.ProductDTO;
import com.example.myshop.ProductMS.exception.MyShopProductException;
import com.example.myshop.ProductMS.service.ProductService;

@RestController
@RequestMapping(value="/product-api")
public class ProductAPI {
	
	@Autowired	
	private ProductService productService;
	
	@Autowired
	private Environment environment;
	
	Log logger=LogFactory.getLog(ProductAPI.class);
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductDTO>> getAllProducts() throws MyShopProductException {
		List<ProductDTO> productList=productService.getAllProducts();
		return new ResponseEntity<List<ProductDTO>>(productList, HttpStatus.OK);
	}
	
	@GetMapping(value="/product/{productId}")
	public ResponseEntity<ProductDTO> getProductById (@PathVariable Integer productId) throws MyShopProductException{
		ProductDTO productDTO=productService.getProductById(productId);
		return new ResponseEntity<>(productDTO,HttpStatus.OK);
	}
	
	@PutMapping(value="/update/{productId}")
	public ResponseEntity<String> reduceAvailableQuantity(@PathVariable Integer productId,
			@RequestBody Integer quantity) throws MyShopProductException{
		productService.reduceAvailableQuantity(productId,quantity);
		return new ResponseEntity<>(environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFULL"),HttpStatus.OK);
	}
	
}

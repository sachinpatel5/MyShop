package com.example.myshop.ProductMS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myshop.ProductMS.dto.ProductDTO;
import com.example.myshop.ProductMS.entity.Product;
import com.example.myshop.ProductMS.exception.MyShopProductException;
import com.example.myshop.ProductMS.repository.ProductRepository;



@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductRepository productRespository;
	
	Log logger=LogFactory.getLog(ProductServiceImpl.class);
	
	@Override
	public List<ProductDTO> getAllProducts() throws MyShopProductException {
		Iterable<Product> products=productRespository.findAll();
		List<ProductDTO> productDTOs=new ArrayList<>();
		products.forEach(product->{
			ProductDTO productDTO =new ProductDTO();
			productDTO.setBrand(product.getBrand());
			productDTO.setCategory(product.getCategory());
			productDTO.setDescription(product.getDescription());
			productDTO.setName(product.getName());
			productDTO.setPrice(product.getPrice());
			productDTO.setProductId(product.getProductId());
			productDTO.setAvailableQuantity(product.getAvailableQuantity());
			
			productDTOs.add(productDTO);
		});
		return productDTOs;
	}

	@Override
	public ProductDTO getProductById(Integer productId) throws MyShopProductException {
		Optional<Product> productOp=productRespository.findById(productId);
		Product product=productOp.orElseThrow(()-> new MyShopProductException("ProductService.PRODUCT_NOT_AVAILABLE"));
		ProductDTO productDTO=new ProductDTO();
		productDTO.setBrand(product.getBrand());
		productDTO.setCategory(product.getCategory());
		productDTO.setDescription(product.getDescription());
		productDTO.setName(product.getName());
		productDTO.setPrice(product.getPrice());
		productDTO.setProductId(product.getProductId());
		productDTO.setAvailableQuantity(product.getAvailableQuantity());
		
		return productDTO;
	}

	@Override
	public void reduceAvailableQuantity(Integer productId, Integer quantity) throws MyShopProductException {
		Optional<Product> productOp=productRespository.findById(productId);
		Product product=productOp.orElseThrow(()-> new MyShopProductException("ProductService.PRODUCT_NOT_AVAILABLE"));
		if(product.getAvailableQuantity()-quantity>=0) {
		product.setAvailableQuantity(product.getAvailableQuantity()-quantity);		
		}else {
			product.setAvailableQuantity(0);
		}
		
		
		productRespository.save(product);
	}

}

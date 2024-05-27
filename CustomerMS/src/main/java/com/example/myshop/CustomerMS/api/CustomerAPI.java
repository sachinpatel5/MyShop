package com.example.myshop.CustomerMS.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.myshop.CustomerMS.dto.CartDTO;
import com.example.myshop.CustomerMS.dto.CartProductDTO;
import com.example.myshop.CustomerMS.dto.CustomerDTO;
import com.example.myshop.CustomerMS.exception.MyShopCustomerException;
import com.example.myshop.CustomerMS.service.CustomerService;

@CrossOrigin
@RestController
@RequestMapping(value="/customer-api")
public class CustomerAPI {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private Environment environment;
	
	static Log logger=LogFactory.getLog(CustomerAPI.class);
	
	@PostMapping(value="/login")
	public ResponseEntity<CustomerDTO> authenticateCustomer(@RequestBody CustomerDTO customerDTO) throws MyShopCustomerException{
		CustomerDTO customerDTOFromDB= customerService.authenticateCustomer(customerDTO.getEmailId(),customerDTO.getPassword());
		return new ResponseEntity<>(customerDTOFromDB,HttpStatus.OK);
	}
	
	@PostMapping(value="/register")
	public ResponseEntity<String> registerCustomer(@RequestBody CustomerDTO customerDTO) throws MyShopCustomerException{
		String registeredWithEmailId=customerService.registerNewCustomer(customerDTO);
		return new ResponseEntity<>(registeredWithEmailId,HttpStatus.OK);
	}
	
	@PutMapping(value="/customer/{customerEmailId:.+}/address/")
	public ResponseEntity<String> updateShippingAddress(@PathVariable("customerEmailId") String customerEmailId,
			@RequestBody String address) throws MyShopCustomerException{
		customerService.updateShippingAddress(customerEmailId,address);
		String modificationSuccessMsg=environment.getProperty("CustomerAPI.UPDATE_ADDRESS_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);
	}
	
	@DeleteMapping(value="/customer/{customerEmailId:.+}")
	public ResponseEntity<String> deleteShippingAddress(@PathVariable("customerEmailId") String customerEmailId) throws MyShopCustomerException{
		customerService.deleteShippingAddress(customerEmailId);
		String modificationSuccessMsg=environment.getProperty("CustomerAPI.DELETE_ADDRESS_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);
	}
	
	@PostMapping(value="/customercarts/add-product")
	public ResponseEntity<String> addProductToCart(@RequestBody CartDTO cartDTO) throws MyShopCustomerException{
		customerService.getCustomerByEmailId(cartDTO.getCustomerEmailId());
		for(CartProductDTO cartProductDTO : cartDTO.getCartProducts()) {
			template.getForEntity("http://ProductMS/MyShop/product-api/product"+cartProductDTO.getProduct().getProductId(), String.class);
		}
	
		ResponseEntity<String> productAddedToCartMessage = template.postForEntity("http://CartMS/MyShop/cart-api/products", cartDTO, String.class);
		return productAddedToCartMessage;
		
	}
	
}

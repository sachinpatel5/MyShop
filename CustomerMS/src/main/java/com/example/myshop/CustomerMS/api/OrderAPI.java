package com.example.myshop.CustomerMS.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.myshop.CustomerMS.dto.CartProductDTO;
import com.example.myshop.CustomerMS.dto.OrderDTO;
import com.example.myshop.CustomerMS.dto.OrderStatus;
import com.example.myshop.CustomerMS.dto.PaymentThrough;
import com.example.myshop.CustomerMS.dto.OrderedProductDTO;
import com.example.myshop.CustomerMS.dto.ProductDTO;
import com.example.myshop.CustomerMS.exception.MyShopCustomerException;
import com.example.myshop.CustomerMS.service.OrderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/customerorder-api")
public class OrderAPI {
	@Autowired
	private OrderService orderService;
	@Autowired
	private Environment environment;
	@Autowired
	private RestTemplate template;

	@PostMapping(value = "/place-order")
	public ResponseEntity<String> placeOrder(@RequestBody OrderDTO order) throws MyShopCustomerException {

		ResponseEntity<CartProductDTO[]> cartProductDTOsResponse = template.getForEntity(
				"http://CartMS/MyShop/cart-api/customer/" + order.getCustomerEmailId() + "/products",
				CartProductDTO[].class);
		CartProductDTO[] cartProductDTOs = cartProductDTOsResponse.getBody();
		template.delete("http://CartMS/MyShop/cart-api/customer" + order.getCustomerEmailId() + "/products");
		List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();
		for (CartProductDTO cartProductDTO : cartProductDTOs) {
			OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
			orderedProductDTO.setProduct(cartProductDTO.getProduct());
			orderedProductDTO.setQuantity(cartProductDTO.getQuantity());
			orderedProductDTOs.add(orderedProductDTO);
		}
		order.setOrderedProducts(orderedProductDTOs);
		Integer orderId = orderService.placeOrder(order);
		String modificationSuccessMsg = environment.getProperty("OrderAPI.ORDER_PLACED_SUCCESSFULLY");
		return new ResponseEntity<>(modificationSuccessMsg + orderId, HttpStatus.CREATED);
	}

	@GetMapping(value = "order/{orderId}")
	public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable Integer orderId) throws MyShopCustomerException {
		OrderDTO orderDTO = orderService.getOrderDetails(orderId);
		for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
			ResponseEntity<ProductDTO> productRespone = template.getForEntity(
					"http://ProductMS/MyShop/product-api/product/" + orderedProductDTO.getProduct().getProductId(),
					ProductDTO.class);
			orderedProductDTO.setProduct(productRespone.getBody());
		}
		return new ResponseEntity<>(orderDTO, HttpStatus.OK);
	}

	@GetMapping(value = "customer/{customerEmailId}/orders")
	public ResponseEntity<List<OrderDTO>> getOrdersOfCustomer(@PathVariable String customerEmailId)
			throws MyShopCustomerException {
		List<OrderDTO> orderDTOs = orderService.findOrdersByCustomerEmailId(customerEmailId);
		for (OrderDTO orderDTO : orderDTOs) {
			for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
				ResponseEntity<ProductDTO> productResponse = template.getForEntity(
						"http://ProductMS/MyShop/product-api/product/" + orderedProductDTO.getProduct().getProductId(),
						ProductDTO.class);
				orderedProductDTO.setProduct(productResponse.getBody());
			}

		}
		return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
	}

	@PutMapping(value = "order/{orderId}/update/order-status")
	public void updateOrderAfterPayment(@PathVariable Integer orderId, @RequestBody String transactionStatus)
			throws MyShopCustomerException {
		if (transactionStatus.equals("TRANSACTION_SUCCESS")) {
			orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
			OrderDTO orderDTO = orderService.getOrderDetails(orderId);
			for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
				template.put("http://CartMS/MyShop/product-api/update/" + orderedProductDTO.getProduct().getProductId(),
						orderedProductDTO.getQuantity());
			}
		} else {
			orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

		}
	}

	@PutMapping(value = "/order/{orderId}/update/payment-through")
	public void updatePaymentOption(@PathVariable Integer orderId, @RequestBody String paymentThrough)
			throws MyShopCustomerException {
		if (paymentThrough.equalsIgnoreCase("DEBIT_CARD")) {
			orderService.updatePaymentThrough(orderId, PaymentThrough.DEBIT_CARD);
		}else {
			orderService.updatePaymentThrough(orderId, PaymentThrough.CREDIT_CARD);

		}
	}
}

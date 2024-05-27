package com.example.myshop.PaymentMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentCircuitBreakerService {
	@Autowired
	private RestTemplate template;
	
	@CircuitBreaker(name="paymentService")
	public void updateOrderAfterPayment(Integer orderId, String transactionStatus) {
		template.put("http://customerMS/MyShop/customerorder-api/order/"+orderId+"update/order-status", transactionStatus);
		
	}

}

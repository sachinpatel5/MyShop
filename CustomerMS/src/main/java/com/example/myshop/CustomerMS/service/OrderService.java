package com.example.myshop.CustomerMS.service;

import java.util.List;

import com.example.myshop.CustomerMS.dto.OrderDTO;
import com.example.myshop.CustomerMS.dto.OrderStatus;
import com.example.myshop.CustomerMS.dto.PaymentThrough;
import com.example.myshop.CustomerMS.exception.MyShopCustomerException;

public interface OrderService {

	Integer placeOrder(OrderDTO order) throws MyShopCustomerException;

	OrderDTO getOrderDetails(Integer orderId) throws MyShopCustomerException;

	List<OrderDTO> findOrdersByCustomerEmailId(String customerEmailId) throws MyShopCustomerException;

	void updateOrderStatus(Integer orderId, OrderStatus cancelled) throws MyShopCustomerException;

	void updatePaymentThrough(Integer orderId, PaymentThrough debitCard) throws MyShopCustomerException;

}

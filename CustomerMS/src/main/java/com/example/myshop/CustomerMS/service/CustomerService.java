package com.example.myshop.CustomerMS.service;

import com.example.myshop.CustomerMS.dto.CustomerDTO;
import com.example.myshop.CustomerMS.exception.MyShopCustomerException;

public interface CustomerService {

	CustomerDTO authenticateCustomer(String emailId, String password) throws MyShopCustomerException;

	String registerNewCustomer(CustomerDTO customerDTO) throws MyShopCustomerException;

	void updateShippingAddress(String customerEmailId, String address) throws MyShopCustomerException;

	void deleteShippingAddress(String customerEmailId) throws MyShopCustomerException;

	CustomerDTO getCustomerByEmailId(String customerEmailId) throws MyShopCustomerException;

}

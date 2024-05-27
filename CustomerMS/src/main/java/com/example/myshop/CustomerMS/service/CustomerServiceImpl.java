package com.example.myshop.CustomerMS.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myshop.CustomerMS.dto.CustomerDTO;
import com.example.myshop.CustomerMS.entity.Customer;
import com.example.myshop.CustomerMS.exception.MyShopCustomerException;
import com.example.myshop.CustomerMS.repository.CustomerRepository;


@Service
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public CustomerDTO authenticateCustomer(String emailId, String password) throws MyShopCustomerException {
		CustomerDTO customerDTO=null;
		Optional<Customer> optionalCustomer=customerRepository.findById(emailId.toLowerCase());
		Customer customer=optionalCustomer.orElseThrow(()-> new MyShopCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		if(!password.equals(customer.getPassword())) {
			throw new MyShopCustomerException("CustomerService.INVALID_CREDENTIALS");
		}
		customerDTO = new CustomerDTO();
		customerDTO.setEmailId(customer.getEmailId());
		customerDTO.setName(customer.getName());
		customerDTO.setPhoneNumber(customer.getPhoneNumber());
		customerDTO.setAddress(customer.getAddress());
		
		return customerDTO;
	}

	@Override
	public String registerNewCustomer(CustomerDTO customerDTO) throws MyShopCustomerException {
		String registeredWithEmailId=null;
		boolean isEmailNotAvailable=customerRepository.findById(customerDTO.getEmailId().toLowerCase()).isEmpty();
		boolean isPhoneNumberNotAvailable=customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isEmpty();
		if(isEmailNotAvailable) {
			if(isPhoneNumberNotAvailable) {
				Customer customer=new Customer();
				customer.setEmailId(customerDTO.getEmailId());
				customer.setName(customerDTO.getName());
				customer.setPassword(customerDTO.getPassword());
				customer.setPhoneNumber(customerDTO.getPhoneNumber());
				customer.setAddress(customerDTO.getAddress());
				customerRepository.save(customer);
				registeredWithEmailId=customer.getEmailId();				
			}else {
				throw new MyShopCustomerException("CustomerService.PHONENUMBER_ALREADY_IN_USE");
			}
		}else {
			throw new MyShopCustomerException("CustomerService.EMAILID_ALREADY_IN_USE");
		}

		return registeredWithEmailId;
	}

	@Override
	public void updateShippingAddress(String customerEmailId, String address) throws MyShopCustomerException {
		Optional<Customer> optionalCustomer=customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer=optionalCustomer.orElseThrow(()-> new MyShopCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(address);
	}

	@Override
	public void deleteShippingAddress(String customerEmailId) throws MyShopCustomerException {
		Optional<Customer> optionalCustomer=customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer=optionalCustomer.orElseThrow(()-> new MyShopCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(null);
		
	}

	@Override
	public CustomerDTO getCustomerByEmailId(String customerEmailId) throws MyShopCustomerException {
		CustomerDTO customerDTO=null;
		Optional<Customer> optionalCustomer=customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer=optionalCustomer.orElseThrow(()-> new MyShopCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		customerDTO =new CustomerDTO();
		customerDTO.setEmailId(customer.getEmailId());
		customerDTO.setName(customer.getName());
		customerDTO.setPhoneNumber(customer.getPhoneNumber());
		customerDTO.setAddress(customer.getAddress());
		return customerDTO;
		
		
	}

}

package com.example.myshop.CustomerMS.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.CustomerMS.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String>{

	List<Customer> findByPhoneNumber(String phoneNumber);

}

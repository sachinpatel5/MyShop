package com.example.myshop.PaymentMS.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.PaymentMS.entity.Card;

public interface CardRepository extends CrudRepository<Card, Integer>{

	List<Card> findByCustomerEmailId(String customerEmailId);

	List<Card> findByCustomerEmailIdAndCardType(String customerEmailId, String cardType);

}

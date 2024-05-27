package com.example.myshop.PaymentMS.service;

import java.util.List;

import com.example.myshop.PaymentMS.dto.CardDTO;
import com.example.myshop.PaymentMS.dto.TransactionDTO;
import com.example.myshop.PaymentMS.exception.MyShopPaymentException;
import com.example.myshop.PaymentMS.exception.PayOrderFallBackException;

public interface PaymentService {

	Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws MyShopPaymentException;

	void updateCustomerCard(CardDTO cardDTO) throws MyShopPaymentException;

	void deleteCustomerCard(String customerEmailId, Integer cardId) throws MyShopPaymentException;

	List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType) throws MyShopPaymentException;

	Integer addTransaction(TransactionDTO transactionDTO) throws MyShopPaymentException,PayOrderFallBackException ;

	TransactionDTO authenticatePayment(String customerEmailId, TransactionDTO transactionDTO) throws MyShopPaymentException;

	CardDTO getCard(Integer cardId) throws MyShopPaymentException;

}

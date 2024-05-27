package com.example.myshop.PaymentMS.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.myshop.PaymentMS.entity.Card;
import com.example.myshop.PaymentMS.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer>{

}

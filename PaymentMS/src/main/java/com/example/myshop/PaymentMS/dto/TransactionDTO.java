package com.example.myshop.PaymentMS.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
	
	private Integer transactionId;
	private OrderDTO order;
	private CardDTO card;
	private Double totalPrice;
	private LocalDateTime transactionDate;
	private TransactionStatus transactionStatus;
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	public OrderDTO getOrder() {
		return order;
	}
	public void setOrder(OrderDTO order) {
		this.order = order;
	}
	public CardDTO getCard() {
		return card;
	}
	public void setCard(CardDTO card) {
		this.card = card;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	
	
}

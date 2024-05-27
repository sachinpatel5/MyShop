package com.example.myshop.PaymentMS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myshop.PaymentMS.dto.CardDTO;
import com.example.myshop.PaymentMS.dto.TransactionDTO;
import com.example.myshop.PaymentMS.dto.TransactionStatus;
import com.example.myshop.PaymentMS.entity.Card;
import com.example.myshop.PaymentMS.entity.Transaction;
import com.example.myshop.PaymentMS.exception.MyShopPaymentException;
import com.example.myshop.PaymentMS.exception.PayOrderFallBackException;
import com.example.myshop.PaymentMS.repository.CardRepository;
import com.example.myshop.PaymentMS.repository.TransactionRepository;
import com.example.myshop.PaymentMS.utility.HashingUtility;

@Service
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws MyShopPaymentException {
		List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
		if (listOfCustomerCards.isEmpty())
			throw new MyShopPaymentException("PaymentService.CUSTOMER_NOT_FOUND");
		cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
		Card newCard = new Card();
		newCard.setCardId(cardDTO.getCardId());
		newCard.setNameOnCard(cardDTO.getNameOnCard());
		newCard.setCardNumber(cardDTO.getCardNumber());
		newCard.setCardType(cardDTO.getCardType());
		newCard.setExpiryDate(cardDTO.getExpiryMonth() + "-" + cardDTO.getExpiryYear());
		newCard.setCvv(cardDTO.getHashCvv());
		newCard.setCustomerEmailId(cardDTO.getCustomerEmailId());
		cardRepository.save(newCard);

		return newCard.getCardId();
	}

	@Override
	public void updateCustomerCard(CardDTO cardDTO) throws MyShopPaymentException {
		Optional<Card> optionalCard=cardRepository.findById(cardDTO.getCardId());
		Card card=optionalCard.orElseThrow(()-> new MyShopPaymentException("PaymentService.CARD_NOT_FOUND"));
		cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
		card.setCardId(cardDTO.getCardId());
		card.setNameOnCard(cardDTO.getNameOnCard());
		card.setCardNumber(cardDTO.getCardNumber());
		card.setCardType(cardDTO.getCardType());
		card.setExpiryDate(cardDTO.getExpiryMonth() + "-" + cardDTO.getExpiryYear());
		card.setCvv(cardDTO.getHashCvv());
		card.setCustomerEmailId(cardDTO.getCustomerEmailId());

	}

	@Override
	public void deleteCustomerCard(String customerEmailId, Integer cardId) throws MyShopPaymentException {
		List<Card> listOfCustomerCards= cardRepository.findByCustomerEmailId(customerEmailId);
		if(listOfCustomerCards.isEmpty())
			throw new MyShopPaymentException("PaymentService.CUSTOMER_NOT_FOUND");
		Optional<Card> optionalCard=cardRepository.findById(cardId);
		Card card=optionalCard.orElseThrow(()-> new MyShopPaymentException("PaymentService.CARD_NOT_FOUND"));
		cardRepository.delete(card);

	}

	@Override
	public List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType)
			throws MyShopPaymentException {
		List<Card> cards=cardRepository.findByCustomerEmailIdAndCardType(customerEmailId,cardType);
		if(cards.isEmpty()) {
			throw new MyShopPaymentException("PaymentService.CARD_NOT_FOUND");
		}
		List<CardDTO> cardDTOs= new ArrayList<CardDTO>();
		for(Card card : cards) {
			CardDTO cardDTO = new CardDTO();
			cardDTO.setCardId(card.getCardId());
			cardDTO.setNameOnCard(card.getNameOnCard());
			cardDTO.setCardNumber(card.getCardNumber());
			cardDTO.setCardType(card.getCardType());
			cardDTO.setHashCvv("XXX");
			String[] expiryDate=card.getExpiryDate().split("-");
			cardDTO.setExpiryMonth(expiryDate[1]);
			cardDTO.setExpiryYear(expiryDate[0]);
			cardDTO.setCustomerEmailId(card.getCustomerEmailId());
			cardDTOs.add(cardDTO);
			
			
		}
		return cardDTOs;
	}

	@Override
	public Integer addTransaction(TransactionDTO transactionDTO) throws MyShopPaymentException, PayOrderFallBackException {
		if(transactionDTO.getTransactionStatus().equals(TransactionStatus.TRANSACTION_FAILED)) {
			throw new PayOrderFallBackException("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
		}
		Transaction transaction=new Transaction();
		transaction.setCardId(transactionDTO.getCard().getCardId());
		transaction.setOrderId(transactionDTO.getOrder().getOrderId());
		transaction.setTransactionDate(transactionDTO.getTransactionDate());
		transaction.setTransactionStatus(transactionDTO.getTransactionStatus());
		transactionRepository.save(transaction);
		
		return transaction.getTransactionId();
	}

	@Override
	public TransactionDTO authenticatePayment(String customerEmailId, TransactionDTO transactionDTO)
			throws MyShopPaymentException {
		if(! transactionDTO.getOrder().getCustomerEmailId().equals(customerEmailId)) {
			throw new MyShopPaymentException("PaymentService.ORDER_DOES_NOT_BELONGS");
		}
		if(! transactionDTO.getOrder().getOrderStatus().equals("PLACED")) {
			throw new MyShopPaymentException("PaymentService.TRANSACTION_ALREADY_DONE");
		}
		CardDTO cardDTO=getCard(transactionDTO.getCard().getCardId());
		if(! cardDTO.getCustomerEmailId().matches(customerEmailId)) {
			throw new MyShopPaymentException("PaymentService.CARD_DOES_NOT_BELONGS");
		}
		if(! cardDTO.getCardType().equals(transactionDTO.getOrder().getPaymentThrough())) {
			throw new MyShopPaymentException("PaymentService.PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE");
			
		}
		if( cardDTO.getHashCvv().equals(HashingUtility.getHashValue(transactionDTO.getCard().getCvv().toString()))) {
			
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_SUCCESS);
		}else {
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_FAILED);
		}
		return transactionDTO;
	}
	
	@Override
	public CardDTO getCard(Integer cardId) throws MyShopPaymentException{
		Optional<Card> optionalCards=cardRepository.findById(cardId);
		Card card=optionalCards.orElseThrow(()-> new MyShopPaymentException("PaymentService.CARD_NOT_FOUND"));
		CardDTO cardDTO=new CardDTO();
		cardDTO.setCardId(card.getCardId());
		cardDTO.setNameOnCard(card.getNameOnCard());
		cardDTO.setCardNumber(card.getCardNumber());
		cardDTO.setCardType(card.getCardType());
		cardDTO.setHashCvv(card.getCvv());
		String[] expiryDate=card.getExpiryDate().split("-");
		cardDTO.setExpiryMonth(expiryDate[1]);
		cardDTO.setExpiryYear(expiryDate[0]);
		cardDTO.setCustomerEmailId(card.getCustomerEmailId());
		return cardDTO;
	}

}

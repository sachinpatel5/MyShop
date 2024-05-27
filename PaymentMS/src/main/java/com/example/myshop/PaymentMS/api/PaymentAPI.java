package com.example.myshop.PaymentMS.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.myshop.PaymentMS.dto.CardDTO;
import com.example.myshop.PaymentMS.dto.OrderDTO;
import com.example.myshop.PaymentMS.dto.TransactionDTO;
import com.example.myshop.PaymentMS.exception.MyShopPaymentException;
import com.example.myshop.PaymentMS.service.PaymentCircuitBreakerService;
import com.example.myshop.PaymentMS.service.PaymentService;

@CrossOrigin
@RestController
@RequestMapping(value="/payment-api")
public class PaymentAPI {
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private Environment environment;
	@Autowired
	private PaymentCircuitBreakerService paymentCircuitBreakerService;
	
	@Autowired
	private RestTemplate template;
	
	public static final Log logger=LogFactory.getLog(PaymentAPI.class);
	
	@PostMapping(value="/customer/{customerEmailId:.+}/cards")
	public ResponseEntity<String> addNewCard(@RequestBody CardDTO cardDTO,
			@PathVariable("customerEmailId")String customerEmailId) throws MyShopPaymentException{
		int cardId;
		cardId=paymentService.addCustomerCard(customerEmailId,cardDTO);
		String message=environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS");
		String toReturn=message+cardId;
		toReturn=toReturn.trim();
		return new ResponseEntity<>(toReturn,HttpStatus.OK);
		
	}
	
	@PutMapping(value="/update/card")
	public ResponseEntity<String> updateCustomerCard(@RequestBody CardDTO cardDTO) throws MyShopPaymentException{
		paymentService.updateCustomerCard(cardDTO);
		String modificationSuccessMsg=environment.getProperty("PaymentAPI.UPDATE_CARD_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);		
	}
	
	@DeleteMapping(value="/customer/{customerEmailId:.+}/card/{cardId}/delete")
	public ResponseEntity<String> deleteCustomerCard(@PathVariable("cardId") Integer cardId, @PathVariable("customerEmailId") String customerEmailId) throws MyShopPaymentException{
		paymentService.deleteCustomerCard(customerEmailId,cardId);
		String modificationSuccessMsg=environment.getProperty("PaymentAPI.DELETE_CARD_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);			
	}
	@GetMapping(value="/customer/{customerEmailId}/card-type/{cardType}")
	public ResponseEntity<List<CardDTO>> getCardsOfCustomer( @PathVariable String customerEmailId, @PathVariable String cardType) throws MyShopPaymentException{
		List<CardDTO> cardDTOs=paymentService.getCustomerCardOfCardType(customerEmailId,cardType);
		return new ResponseEntity<>(cardDTOs,HttpStatus.OK);			
	}
	
	@PostMapping(value="/customer/{customerEmailId}/pay-order")
	public ResponseEntity<String> payForOrder(@PathVariable("customerEmailId") String customerEmailId,@RequestBody TransactionDTO transactionDTO) throws MyShopPaymentException{
		ResponseEntity<OrderDTO> orderDetails=template.getForEntity("http://CustomerMS/MyShop/customerorder-api/order"+transactionDTO.getOrder().getOrderId(), OrderDTO.class);
		transactionDTO.setTransactionDate(orderDetails.getBody().getDateOfOrder());
		transactionDTO.setTotalPrice(orderDetails.getBody().getTotalPrice());
		paymentService.authenticatePayment(customerEmailId,transactionDTO);
		paymentService.addTransaction(transactionDTO);
		paymentCircuitBreakerService.updateOrderAfterPayment(transactionDTO.getOrder().getOrderId(),transactionDTO.getTransactionStatus().toString());
		String message=environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_ONE")+transactionDTO.getTotalPrice()+environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_TWO")+transactionDTO.getTotalPrice()+environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_THREE"+transactionDTO.getTransactionId());
		return new ResponseEntity<>(message,HttpStatus.OK);
				
	}
	
	public ResponseEntity<String> payForOrderFallBack(String customerEmailId, TransactionDTO transactionDTO, RuntimeException exception){
		String message="";
		if(exception.getMessage().equals("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING"))
			message=environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
		else if(exception.getMessage().contains("Order Not Found"))
			throw new RestClientException(exception.getMessage());
		else 
			message=environment.getProperty("PaymentAPI.PAYMENT_FAILURE_FALLBACK");
		return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
	}
}

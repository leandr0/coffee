package com.lrgoncalves.coffee.model;

import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lrgoncalves.coffee.model.neo4j.Neo4jSessionFactory;

@NodeEntity(label = "PAYMENT")
public class Payment extends NodeIdentifier{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3655680606624893060L;

	private static final Logger LOG = LoggerFactory.getLogger(Payment.class);
	
	@Property
	private double		amount;
	
	@Property
    private String 		cardholderName;
	
	@Property
    private String 		cardNumber;
	
	@Property
    private int 		expiryMonth;
	
	@Property
    private int 		expiryYear;
	
	@Property
    private Date	paymentDate;
	
	@Property
	private String authorizationCode;

	@Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
	private Order order;
	
	public Payment() {}
	

	
	
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}




	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}




	/**
	 * @return the cardholderName
	 */
	public String getCardholderName() {
		return cardholderName;
	}




	/**
	 * @param cardholderName the cardholderName to set
	 */
	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
	}




	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}




	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}




	/**
	 * @return the expiryMonth
	 */
	public int getExpiryMonth() {
		return expiryMonth;
	}




	/**
	 * @param expiryMonth the expiryMonth to set
	 */
	public void setExpiryMonth(int expiryMonth) {
		this.expiryMonth = expiryMonth;
	}




	/**
	 * @return the expiryYear
	 */
	public int getExpiryYear() {
		return expiryYear;
	}




	/**
	 * @param expiryYear the expiryYear to set
	 */
	public void setExpiryYear(int expiryYear) {
		this.expiryYear = expiryYear;
	}




	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}




	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}




	/**
	 * @return the authorizationCode
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}




	/**
	 * @param authorizationCode the authorizationCode to set
	 */
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}




	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}




	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}


	public void generateUthorizationCode() {
		authorizationCode = UUID.randomUUID().toString();
	}

	/**
	 * 
	 * @param payment
	 * @return Order
	 */
	public Payment save (Payment payment) {

			
		payment.setPaymentDate(DateTime.now().toDate());
		payment.generateUthorizationCode();
		
		LOG.debug("Persisting Payment");
		Neo4jSessionFactory.getInstance().getNeo4jSession().save(payment,2);
		return find(payment.getId());
	}

	/**
	 * 
	 * @param id
	 * @return Order
	 */
	private Payment find(Long id) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(Payment.class, id, 3);
	}
}

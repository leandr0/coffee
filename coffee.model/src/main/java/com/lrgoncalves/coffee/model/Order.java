package com.lrgoncalves.coffee.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.mongodb.trace.BusinessOperationType;
import com.lrgoncalves.coffee.model.mongodb.trace.TraceType;
import com.lrgoncalves.coffee.model.neo4j.Neo4jSessionFactory;
import com.lrgoncalves.coffee.model.type.LocationType;
import com.lrgoncalves.coffee.model.type.OrderStatusType;
import com.lrgoncalves.mongodb.MongoDBInsert;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@NodeEntity(label= "ORDER")
public class Order extends NodeIdentifier implements JsonModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4294844492455739498L;

	private static final Logger LOG = LoggerFactory.getLogger(Order.class);

	@Relationship(direction = Relationship.OUTGOING, type = "TAKE_AWAY" )
	private Location location;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private List<Item> items;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private OrderStatus status;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Reference reference;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Payment payment;

	@Property
	private double price; 

	public Order(){
		location = new Location(LocationType.IN_STORE);
		items = new LinkedList<Item>();
		status	= new OrderStatus(OrderStatusType.UNPAID);
	}


	public Order(Location location, List<Item> items) {
		this(location, new OrderStatus(OrderStatusType.UNPAID), items);
	}


	public Order(Location location, OrderStatus status, List<Item> items) {
		this.location = location;
		this.items = items;
		this.status = status;
	}


	public Location getLocation() {
		return location;
	}

	public List<Item> getItems() {
		return items;
	}

	private double calculateCost() {
		double total = 0.0;
		if (items != null) {
			for (Item item : items) {
				if(item != null && item.getDrink() != null) {
					total += item.getDrink().getType().getPrice();
				}
			}
		}
		return total;
	}

	public void updatePrice() {
		this.setPrice(this.getPrice());
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Location: " + location + "\n");
		sb.append("Status: " + status + "\n");
		for(Item i : items) {
			sb.append("Item: " + i.toString()+ "\n");
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	@Override
	public String toJson() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		return mapper.writeValueAsString(this);

	}

	/**
	 * @return the reference
	 */
	public Reference getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(Reference reference) {
		this.reference = reference;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<Item> items) {
		this.items = items;
	}

	/**
	 * @return the payment
	 */
	public Payment getPayment() {
		return payment;
	}


	/**
	 * @param payment the payment to set
	 */
	public void setPayment(Payment payment) {
		this.payment = payment;
	}


	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}


	/**
	 * 
	 * @param order
	 * @return Order
	 */
	public Order save (Order order) {

		LOG.info("Calculating cost");
		order.setPrice(order.calculateCost());

		if(order.getReference() == null){			
			order.setReference(new Reference());
		}

		if( StringUtils.isBlank(order.getReference().getUUDI())){
			LOG.debug("Generating UUDI");
			order.getReference().generateUUDI();
		}

		LOG.debug("Persisting Order");
		Neo4jSessionFactory.getInstance().getNeo4jSession().save(order);

		order = find(order.getId(),3);

		return   order;
	}

	/**
	 * 
	 * @param id
	 * @return Order
	 */
	public Order find(Long id) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(Order.class, id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Order find(Long id, int depth) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(Order.class, id, depth);
	}

	/**
	 * 	MATCH (ord:ORDER)-[rel:HAS]-> (ref:REFERENCE)
	 *	WHERE ref.UUDI = '5031f826-7807-4912-ac80-c133d976fb44'
	 *	RETURN  (ord)-[ *..2]-()
	 */
	public Order findByUUDI(final String UUDI) {

		String cypher = "MATCH (ord:ORDER)-[rel:HAS]-> (ref:REFERENCE {UUDI:{_UUDI}}) RETURN ord"; 

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("_UUDI", UUDI);

		QueryResultModel result = (QueryResultModel) Neo4jSessionFactory.getInstance().getNeo4jSession().query(cypher, parameters);

		Order order = null;

		for(Map<String,Object> map : result.queryResults()) {

			for(String set : map.keySet()) {
				order =  (Order) map.get(set);

			}
		}  	

		if(order != null) {
			order = order.find(order.getId(),3);
		}		

		return order;

	}

	public static String tracePersist(TraceType type, BusinessOperationType businessOperationType ,final String serviceRequest) {

		MongoDBInsert instance = new MongoDBInsert("order");

		BasicDBObject request = new BasicDBObject();

		request.append("type", type.getValue());
		request.append("business_operation", businessOperationType.toString());

		Date now = new Date();

		request.append("date", now);

		BasicDBObject payload = new BasicDBObject();
		payload.append("type", "json");

		DBObject content = null;

		if(type == TraceType.RESPONSE_ERROR) {
			content = new BasicDBObject();
			content.put("error", (DBObject) JSON.parse(serviceRequest));
		}else {
			content = (DBObject) JSON.parse(serviceRequest);
		}

		payload.append("content", content);

		request.append("payload", payload);

		DBObject result = instance.insert(request);

		return result.toString();
	}
}
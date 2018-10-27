/**
 * 
 */
package com.lrgoncalves.coffee.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.lrgoncalves.coffee.model.neo4j.Neo4jSessionFactory;
import com.lrgoncalves.coffee.model.type.OrderStatusType;

/**
 * @author root
 *
 */
@NodeEntity(label = "STATUS")
public class OrderStatus extends NodeIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8630850917955153376L;
	
	@Property
	private OrderStatusType type;
	
	public OrderStatus() {}
	
	public OrderStatus(OrderStatusType type) {
		this.type = type;
	}
	
	public OrderStatus(final String status) {
		this.type = OrderStatusType.valueOf(status);
	}

	/**
	 * @return the type
	 */
	public OrderStatusType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OrderStatusType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @param item
	 * @return Item
	 */
	public OrderStatus save (OrderStatus status) {
		Neo4jSessionFactory.getInstance().getNeo4jSession().save(status);
		return find(status.getId());
	}

	/**
	 * 
	 * @param id
	 * @return Item
	 */
	public OrderStatus find(Long id) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(OrderStatus.class, id);
	}
}

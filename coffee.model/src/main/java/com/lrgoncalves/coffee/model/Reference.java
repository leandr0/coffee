/**
 * 
 */
package com.lrgoncalves.coffee.model;

import java.util.UUID;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author lrgoncalves
 *
 */
@NodeEntity(label = "REFERENCE")
public class Reference extends NodeIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1196290340983807271L;

	@Property
	private String UUDI;

	@Relationship(direction = Relationship.OUTGOING, type = "BELONGS_TO")
	private Order order;

	public Reference() {
	}

	/**
	 * @return the UUDI
	 */
	public String getUUDI() {
		return UUDI;
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

	public void generateUUDI() {
		UUDI = UUID.randomUUID().toString();
	}
}

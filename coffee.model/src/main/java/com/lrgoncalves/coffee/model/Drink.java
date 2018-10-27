/**
 * 
 */
package com.lrgoncalves.coffee.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.lrgoncalves.coffee.model.type.DrinkType;

/**
 * @author root
 *
 */
@NodeEntity(label = "DRINK")
public class Drink extends NodeIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6559734302165014623L;
	
	@Property(name = "type")
	private DrinkType type;

	public Drink() {}
	
	public Drink(DrinkType type) {
		this.type = type;
	}
	
	public Drink(final String drink) {
		this.type = DrinkType.valueOf(drink);
	}
	
	/**
	 * @return the type
	 */
	public DrinkType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(DrinkType type) {
		this.type = type;
	}
}

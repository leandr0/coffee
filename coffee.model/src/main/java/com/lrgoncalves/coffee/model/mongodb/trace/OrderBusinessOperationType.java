/**
 * 
 */
package com.lrgoncalves.coffee.model.mongodb.trace;

/**
 * @author lrgoncalves
 *
 */
public enum OrderBusinessOperationType implements BusinessOperationType{

	CREATE_ORDER("create_order"),

	FIND_ORDER("find_order");
	
	
	final String value;

	OrderBusinessOperationType(String value) { 
		this.value = value; 
	}

	public String getValue() {
		return this.value; 
	}
	
	@Override
	public String toString() {
		return getValue();
	}
}

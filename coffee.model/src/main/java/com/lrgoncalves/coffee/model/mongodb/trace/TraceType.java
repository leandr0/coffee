/**
 * 
 */
package com.lrgoncalves.coffee.model.mongodb.trace;

/**
 * @author lrgoncalves
 *
 */
public enum TraceType {

	REQUEST("request"),

	RESPONSE("response"),

	RESPONSE_ERROR("response");
	
	final String value;

	TraceType(String value) { 
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

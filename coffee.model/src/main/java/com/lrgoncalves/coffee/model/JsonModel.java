/**
 * 
 */
package com.lrgoncalves.coffee.model;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author lrgoncalves
 *
 */
public interface JsonModel {

	public String toJson() throws JsonProcessingException ;	
}

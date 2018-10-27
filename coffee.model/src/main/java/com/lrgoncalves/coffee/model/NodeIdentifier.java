/**
 * 
 */
package com.lrgoncalves.coffee.model;

import java.io.Serializable;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author root
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class NodeIdentifier implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1554514616752978109L;
	
	@Property
	@Id @GeneratedValue
	private Long id;


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
}

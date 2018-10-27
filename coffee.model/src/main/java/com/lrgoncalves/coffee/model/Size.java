/**
 * 
 */
package com.lrgoncalves.coffee.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.lrgoncalves.coffee.model.type.SizeType;

/**
 * @author root
 *
 */
@NodeEntity(label = "SIZE")
public class Size extends NodeIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -266073362278280958L;
	
	@Property
	private SizeType type;

	public Size() {}
	
	public Size(SizeType type) {
		this.type = type;
	}
	
	public Size(final String size) {
		this.type = SizeType.valueOf(size);
	}
	
	/**
	 * @return the type
	 */
	public SizeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SizeType type) {
		this.type = type;
	}
	
}

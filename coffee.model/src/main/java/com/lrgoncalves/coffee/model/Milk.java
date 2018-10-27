/**
 * 
 */
package com.lrgoncalves.coffee.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.lrgoncalves.coffee.model.type.MilkType;

/**
 * @author root
 *
 */
@NodeEntity(label = "MILK")
public class Milk extends NodeIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1314922344108962293L;
	
	@Property
	private MilkType type;

	public Milk() {}
	
	public Milk (MilkType type) {
		this.type = type;
	}
	
	public Milk (final String milk) {
		this.type = MilkType.valueOf(milk);
	}
	
	/**
	 * @return the type
	 */
	public MilkType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MilkType type) {
		this.type = type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = MilkType.valueOf(type);
	}
}

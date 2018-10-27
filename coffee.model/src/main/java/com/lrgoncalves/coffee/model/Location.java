/**
 * 
 */
package com.lrgoncalves.coffee.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.lrgoncalves.coffee.model.type.LocationType;

/**
 * @author root
 *
 */
@NodeEntity(label = "LOCATION")
public class Location extends NodeIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 667884798620734192L;
	
	@Property
	private LocationType type;

	public Location() {}
	
	public Location(LocationType type) {
		this.type = type;
	}
	
	public Location(final String location) {
		type = LocationType.valueOf(location);
	}
	
	/**
	 * @return the type
	 */
	public LocationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(LocationType type) {
		this.type = type;
	}
}

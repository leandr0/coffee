/**
 * 
 */
package com.lrgoncalves.coffee.model.hateos;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lrgoncalves.coffee.model.Item;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.type.OrderStatusType;

/**
 * @author lrgoncalves
 *
 */
public class ItemHATEOS extends HATEOS <Item> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5146863630144006601L;

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ItemHATEOS.class);
	
	
	private ItemHATEOS instance;

	public String generateHATEOS(Order order, URI uri) throws URISyntaxException, JsonProcessingException {
			
		if(order.getItems() != null &&  !order.getItems().isEmpty()) {
			
			instance = new ItemHATEOS();
		
			instance.links = new LinkedHashSet<Link>();
			
			for (Item item : order.getItems()) {
				item.setOrder(order);
				generateHATEOS(item, uri);
			}
			
			return instance.toJson();
		}
		//TODO: tratar isso
		return "";
	}


	@Override
	public String generateHATEOS(Item model, URI uri) throws URISyntaxException, JsonProcessingException {


		if(model == null ) {
			LOG.warn("Invalid parameter to generate HATEOS");
			throw new IllegalArgumentException();
		}

		if(model.getOrder().getStatus().getType() != OrderStatusType.UNPAID) {
			LOG.warn("Invalid status to update");
			throw new IllegalArgumentException();	
		}
		
		final String key = model.getOrder().getReference().getUUDI();


		instance.getLinks().add(generateSelfLink(instance, uri, key));

		Link cancel = instance.new Link();
		LOG.info("Generating cancel link");
		cancel.setHeaders(generateHeader("Accept", "application/json"))
			.setHref(new URI(uri+"order/"+key+"/item/"+model.getId()))
			.setMethod("DELETE")
			.setRel("cancel");
		
		Link update = instance.new Link();
		LOG.info("Generating update link");
		update.setHeaders(generateHeader("Accept", "application/json"))
			.setHref(new URI(uri+"order/"+key+"/item/"+model.getId()))
			.setMethod("PATCH")
			.setRel("update");

		instance.getLinks().add(update);
		instance.getLinks().add(cancel);
		
		model.setOrder(null);

		return instance.toJson(model);
	}

}

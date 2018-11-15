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
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.type.OrderStatusType;

/**
 * @author lrgoncalves
 *
 */
public class OrderHATEOS extends HATEOS <Order>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -884543362130453512L;

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OrderHATEOS.class);

	/**
	 * 
	 * @param order
	 * @return
	 * @throws URISyntaxException
	 * @throws JsonProcessingException 
	 */
	public String generateHATEOS(final Order order, final URI uri) throws URISyntaxException, JsonProcessingException {


		if(order == null || order.getStatus() == null || order.getStatus().getType() == null) {
			LOG.warn("Invalid parameter to generate HATEOS");
			throw new IllegalArgumentException("Order Not Found");
		}

		OrderHATEOS instance = new OrderHATEOS();

		instance.links = new LinkedHashSet<Link>();

		final String key = order.getReference().getUUDI();

		OrderStatusType status =  order.getStatus().getType();

		instance.getLinks().add(generateSelfLink(instance, uri, key));
		
		switch (status) {
		case PREPARING :
			/**For future implementations**/
			break;
		case READY :
			/**For future implementations**/
			break;

		case TAKEN :
			/**For future implementations**/
			break;

		case UNPAID :
			
			Link payment = instance.new Link();
			LOG.info("Generating payment link");
			payment.setHeaders(generateHeader("Accept", "application/json"))
				.setHref(new URI(uri+"payment/order/"+key))
				.setMethod("POST")
				.setRel("payment");
			
			payment.getHeaders().put("Referer", generateSelfURI(uri, key).toString());
			
			instance.getLinks().add(payment);
			
			Link cancel = instance.new Link();
			LOG.info("Generating cancel link");
			cancel.setHeaders(generateHeader("Accept", "application/json"))
				.setHref(new URI(uri+"order/"+key+"/cancel"))
				.setMethod("DELETE")
				.setRel("cancel");
			
			instance.getLinks().add(cancel);
			
			Link update = instance.new Link();
			LOG.info("Generating update link");
			update.setHeaders(generateHeader("Accept", "application/json"))
				.setHref(new URI(uri+"order/"+key+"/update"))
				.setMethod("PATCH")
				.setRel("update");
			
			instance.getLinks().add(update);
			
			break;
		case CANCELLED	:
			/**For future implementations**/
			break;
			
		}

		return instance.toJson(order);
	}
}
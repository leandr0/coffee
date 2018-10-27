/**
 * 
 */
package com.lrgoncalves.microservices.coffee.order;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.Item;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.hateos.ItemHATEOS;
import com.lrgoncalves.coffee.model.hateos.OrderHATEOS;
import com.lrgoncalves.coffee.model.type.OrderStatusType;

/**
 * @author lrgoncalves
 *
 */
@Path("/order/{UUDI}/item")
public class ItemService {
	
	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ItemService.class);

	/**
	 * 
	 */
	private @Context UriInfo uriInfo;
	
	/**
	 * 
	 */
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * 
	 */
	private static ItemHATEOS hateos = new ItemHATEOS();
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrderItem(final @PathParam(value="UUDI") String uudi,final String itemRequestJson ) {
		
		LOG.info("Receiving Order Request Payload in method updateOrder");

		Order order = new Order();
		
		try {

			LOG.info("Finding order by UUDI");
			order = order.findByUUDI(uudi);

			if(order.getStatus().getType() == OrderStatusType.UNPAID) {

				OrderHATEOS orderHATEOS = new OrderHATEOS();
				
				List<Item> items = null;
				
				LOG.info("Converting JSON Payload to Object");
				items = mapper.readValue(itemRequestJson, new TypeReference<List<Item>>() {});
				
				 
				for (Item item : items) {
					order.getItems().add(item);
				}
				 
				order = order.save(order);
				
				LOG.info("Generating HATEOS");
				final String HATEOS = hateos.generateHATEOS(order,uriInfo.getBaseUri());
				LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status "+Status.OK);
				return Response.status(Status.OK).entity(HATEOS).build();

			}else {

				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status"+Status.NOT_ACCEPTABLE);
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}

		} catch (JsonParseException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}
	
	
	
	
	@PATCH
	@Path("/{ITEM_ID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrderItem(final @PathParam(value="UUDI") String uudi,final @PathParam(value="ITEM_ID") String itemId,final String itemRequestJson ) {
		
		LOG.info("Receiving Order Request Payload in method updateOrder");

		Order order = new Order();
		
		try {

			LOG.info("Saving order request");
			order = order.findByUUDI(uudi);

			if(order.getStatus().getType() == OrderStatusType.UNPAID) {

				
				
				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);
		
				Item item = new Item();
				
				LOG.info("Converting JSON Payload to Object");
				item = mapper.readValue(itemRequestJson, Item.class);
				
				item.save(item);
				
				order.updatePrice();
				
				order.save(order);
				

				LOG.info("Returning HTTP Status "+Status.OK);
				return Response.status(Status.OK).build();

			}else {

				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status"+Status.NOT_ACCEPTABLE);
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}

		} catch (JsonParseException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}

	
	@DELETE
	@Path("/{ITEM_ID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteOrderItem(final @PathParam(value="UUDI") String uudi,final @PathParam(value="ITEM_ID") Long itemId ) {
		
		LOG.info("Receiving Order Request Payload in method updateOrder");

		Order order = new Order();

		try {

			LOG.info("Fiding order request");
			order = order.findByUUDI(uudi);

			if(order.getStatus().getType() == OrderStatusType.UNPAID) {
				
				Item item = new Item();
				
				LOG.info("Deletind item : "+itemId );
				item.deleteItemFull(itemId);
				
				LOG.info("Update order preice");
				order.updatePrice();
				LOG.info("Updating order");
				order.save(order);
				
				LOG.info("Returning HTTP Status "+Status.OK);
				return Response.status(Status.OK).build();

			}else {

				LOG.info("Returning HTTP Status"+Status.NOT_ACCEPTABLE);
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}
}
package com.lrgoncalves.microservices.coffee.order;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.OrderStatus;
import com.lrgoncalves.coffee.model.hateos.HATEOS;
import com.lrgoncalves.coffee.model.hateos.OrderHATEOS;
import com.lrgoncalves.coffee.model.type.OrderStatusType;

@Path("/order")
public class OrderService {

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

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
	private static OrderHATEOS hateos = new OrderHATEOS();
	
	@GET
	@Path("/{UUDI}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOrder(@PathParam(value="UUDI") String uudi, final String orderRequestJson) {

		if(orderRequestJson != null && StringUtils.isNotEmpty(orderRequestJson)) {
			LOG.info("payload >>>>  "+ orderRequestJson );
			System.out.println("payload >>>>  "+ orderRequestJson);
			
			return null;
		}
		
		
		LOG.info("Receiving Order Request Payload in method getOrder");
		LOG.debug(uudi);

		Order order = new Order();

		try {

			LOG.info("Fiding order request");
			order = order.findByUUDI(uudi);

			LOG.info("Generating HATEOS");
			String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
			LOG.debug(result);
	        
			LOG.info("Returning HTTP Status "+Status.OK);
			return Response.status(Status.OK).entity(result).build();

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}


	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrder(final String orderRequestJson) {

		LOG.info("Receiving Order Request Payload in method createOrder");
		LOG.debug(orderRequestJson);

		Order order = null;

		try {

			if(StringUtils.isBlank(orderRequestJson)) {
				LOG.debug("Payload request is null or empty.");
				throw new IllegalArgumentException("Bad Request");
			}
			
			LOG.info("Converting JSON Payload to Object");
			order = mapper.readValue(orderRequestJson, Order.class);

			LOG.info("Saving order request");
			order.save(order);

			LOG.info("Generating HATEOS");
			String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
			LOG.debug(result);

			LOG.info("Returning HTTP Status "+Status.CREATED);
			return Response.status(Status.CREATED).entity(result).build();

		}catch (IllegalArgumentException i) {
			LOG.error(i.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} 
		catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}

	@DELETE
	@Path("/{UUDI}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response cancelOrder(final @PathParam(value="UUDI") String uudi ) {

		LOG.info("Receiving Order Request Payload in method deleteOrder");

		Order order = new Order();

		try {

			LOG.info("Fiding order request");
			order = order.findByUUDI(uudi);
			
			if(order == null) {
				LOG.info("Returning HTTP Status 404");
				return Response.status(Status.NOT_FOUND).entity("{\"messsage\": \"Resource Not Found.\" }").build();
			}

			if(order.getStatus().getType() == OrderStatusType.UNPAID) {

				OrderStatus status = order.getStatus();
				
				status.setType(OrderStatusType.CANCELLED);
				LOG.info("Updating Status to CANCELLED");
				order.setStatus(status.save(status));
				
				LOG.info("Generating HATEOS");
				String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
				LOG.debug(result);

				LOG.info("Returning HTTP Status "+Status.OK);
				return Response.status(Status.OK).entity(result).build();

			}else {

				LOG.info("Generating HATEOS");
				String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
				LOG.debug(result);

				LOG.info("Returning HTTP Status 403");
				return Response.status(Status.FORBIDDEN).entity(result).build();
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}
	
	/**
	 * Just the location could be updated
	 * @param uudi
	 * @return
	 */
	@PATCH
	@Path("/{UUDI}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrder(final @PathParam(value="UUDI") String uudi ) {
		
		LOG.info("Receiving Order Request Payload in method updateOrder");

		Order order = new Order();

		try {

			LOG.info("Saving order request");
			order = order.findByUUDI(uudi);

			if(order.getStatus().getType() == OrderStatusType.UNPAID) {

				
				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status "+Status.OK);
				return Response.status(Status.OK).entity("").build();

			}else {

				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status"+Status.NOT_ACCEPTABLE);
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}	
}

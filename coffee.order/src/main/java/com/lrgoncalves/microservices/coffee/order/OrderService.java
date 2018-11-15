package com.lrgoncalves.microservices.coffee.order;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.*;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.OrderStatus;
import com.lrgoncalves.coffee.model.hateos.OrderHATEOS;
import static com.lrgoncalves.coffee.model.mongodb.trace.OrderBusinessOperationType.*;
import static com.lrgoncalves.coffee.model.mongodb.trace.OrderTrace.*;
import static com.lrgoncalves.coffee.model.mongodb.trace.TraceType.*;
import static com.lrgoncalves.coffee.model.type.OrderStatusType.*;

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
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	public Response getOrder(@PathParam(value="UUDI") String uudi) {

		try {

			
			String pathParam = "{ \"path_parameters\" :  [ { \"UUDI\" : \""+uudi+"\" }] }";
			
			storePayloadAsync(REQUEST, FIND_ORDER , pathParam);

			if(uudi == null && StringUtils.isEmpty(uudi)) {
				LOG.debug("UUDI request is null or empty.");
				throw new IllegalArgumentException("Bad Request");
			}

			LOG.info("Receiving Order Request Payload in method getOrder");
			LOG.debug(uudi);

			Order order = new Order();

			LOG.info("Fiding order request");
			order = order.findByUUDI(uudi);

			LOG.info("Generating HATEOS");
			String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
			LOG.debug(result);

			storePayloadAsync(RESPONSE, FIND_ORDER , result);
			
			LOG.info("Returning HTTP Status "+ OK);
			return Response.status(OK).entity(result).build();

		}catch (IllegalArgumentException i) {
			
			LOG.error(i.getMessage());
			
			try {				
				storePayloadAsync(RESPONSE_ERROR,FIND_ORDER,buildErrorResponse(Status.NOT_FOUND));
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
			
			return Response.status(NOT_FOUND).entity(i.getMessage()).build();
		} 
		catch (Throwable t) {
			
			LOG.error(t.getMessage());
			
			try {				
				storePayloadAsync(RESPONSE_ERROR,FIND_ORDER,buildErrorResponse(INTERNAL_SERVER_ERROR));
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
			
			return Response.serverError().entity(t.getMessage()).build();
		}
	}


	@POST
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	public Response createOrder(final String orderRequestJson) {

		LOG.info("Receiving Order Request Payload in method createOrder");
		LOG.debug(orderRequestJson);

		Order order = null;

		try {

			storePayloadAsync(REQUEST, CREATE_ORDER ,orderRequestJson);

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

			storePayloadAsync(RESPONSE, CREATE_ORDER ,result);

			LOG.info("Returning HTTP Status "+ CREATED);
			return Response.status(CREATED).entity(result).build();

		}catch (IllegalArgumentException i) {
			
			LOG.error(i.getMessage());

			Response response = Response.status(BAD_REQUEST).build();
			try {				
				storePayloadAsync(RESPONSE_ERROR,CREATE_ORDER,buildErrorResponse(BAD_REQUEST));
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
			return response;
		} 
		catch (Throwable t) {
			
			LOG.error(t.getMessage());

			Response response = Response.status(INTERNAL_SERVER_ERROR).build();
			try {
				storePayloadAsync(RESPONSE_ERROR,CREATE_ORDER,buildErrorResponse(INTERNAL_SERVER_ERROR));
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}

			return response;
		}
	}

	@DELETE
	@Path("/{UUDI}")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	public Response cancelOrder(final @PathParam(value="UUDI") String uudi ) {

		LOG.info("Receiving Order Request Payload in method deleteOrder");

		Order order = new Order();

		try {

			LOG.info("Fiding order request");
			order = order.findByUUDI(uudi);

			if(order == null) {
				LOG.info("Returning HTTP Status 404");
				return Response.status(NOT_FOUND).entity("{\"messsage\": \"Resource Not Found.\" }").build();
			}

			if(order.getStatus().getType() == UNPAID) {

				OrderStatus status = order.getStatus();

				status.setType(CANCELLED);
				LOG.info("Updating Status to CANCELLED");
				order.setStatus(status.save(status));

				LOG.info("Generating HATEOS");
				String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
				LOG.debug(result);

				LOG.info("Returning HTTP Status "+ OK);
				return Response.status(OK).entity(result).build();

				
			}else {

				LOG.info("Generating HATEOS");
				String result = hateos.generateHATEOS(order,uriInfo.getBaseUri());
				LOG.debug(result);

				LOG.info("Returning HTTP Status 403");
				return Response.status(FORBIDDEN).entity(result).build();
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
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	public Response updateOrder(final @PathParam(value="UUDI") String uudi ) {

		LOG.info("Receiving Order Request Payload in method updateOrder");

		Order order = new Order();

		try {

			LOG.info("Saving order request");
			order = order.findByUUDI(uudi);

			if(order.getStatus().getType() == UNPAID) {


				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status "+ OK);
				return Response.status(OK).entity("").build();

			}else {

				LOG.info("Generating HATEOS");
				//final String HATEOS = OrderHATEOS.generateHATEOS(order,uriInfo.getBaseUri()).toJson();
				//LOG.debug(HATEOS);

				LOG.info("Returning HTTP Status"+ NOT_ACCEPTABLE);
				return Response.status(NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}	

	private String buildErrorResponse(final Status httpStatus) {
		return "{\"Status Code\" : \""+httpStatus.getStatusCode()+" "+httpStatus.getReasonPhrase()+"\"}";
	}
}
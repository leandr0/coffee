/**
 * 
 */
package com.lrgoncalves.microservices.payment;

import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.Payment;
import com.lrgoncalves.coffee.model.type.OrderStatusType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

/**
 * @author lrgoncalves
 *
 */
@Path("/payment")
public class PaymentService {

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

	/**
	 * 
	 */
	private @Context UriInfo uriInfo;



	/**
	 * 
	 */
	private static ObjectMapper mapper = new ObjectMapper();

	@POST
	@Path("/order/{UUDI}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response creditCardPayment(@HeaderParam("Referer") String orderLink  ,final @PathParam("UUDI") String  uudi ,final String paymentRequestJson) {

		try {


			if(StringUtils.isBlank(orderLink)) {
				//throw new IllegalArgumentException("Invalid request");
				return Response.status(Status.BAD_REQUEST).entity("{\"messsage\": \"Invalid Request\" }").build();
			}

			HttpResponse<JsonNode> jsonOrder = Unirest.get(orderLink)
					.header("Content-Type", "application/json").asJson();

			LOG.info("Generating HATEOS");
			//TODO : Generating Hypermedia for Payment
			//TODO : Generating standard message for error or warning (internal error and business messages)
			//final String HATEOS = OrderHATEOS.generateHATEOS(,uriInfo.getBaseUri()).toJson();
			//LOG.debug(HATEOS);

			String response = jsonOrder.getBody().toString();

			if(StringUtils.isBlank(paymentRequestJson)) {
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Request\" }").build();
			}

			Payment payment = mapper.readValue(paymentRequestJson, Payment.class);

			double price = getPriceFromOrder(jsonOrder);			

			if(payment.getAmount() != price) {

				LOG.info("Returning HTTP Status 406");
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"orderPrice\": "+price+" , \"sentAmount\": "+payment.getAmount()+" }").build();
			}

			payment.setOrder(getOrder(jsonOrder));

			if(payment.getOrder().getStatus().getType() != OrderStatusType.UNPAID) {
				LOG.info("Returning HTTP Status 406");
				return Response.status(Status.NOT_ACCEPTABLE).entity("{\"messsage\": \"Invalid Status\" }").build();
			}


			payment.getOrder().getStatus().setType(OrderStatusType.PREPARING);

			payment.save(payment);

			LOG.info("Returning HTTP Status 201");
			return Response.status(Status.CREATED).entity(response).build();

		} catch (Throwable t) {
			LOG.error(t.getMessage());
			return Response.serverError().build();
		}
	}

	private double getPriceFromOrder(HttpResponse<JsonNode> jsonOrder) {

		Iterator<Object> jsonIterator = jsonOrder.getBody().getArray().iterator();

		while(jsonIterator.hasNext()) {
			JSONObject jsonObject = (JSONObject) jsonIterator.next();
			return (double) jsonObject.get("price");
		}

		return 0.0;
	}


	private Order getOrder(HttpResponse<JsonNode> jsonOrder) {

		Iterator<Object> jsonIterator = jsonOrder.getBody().getArray().iterator();

		while(jsonIterator.hasNext()) {
			JSONObject jsonObject = (JSONObject) jsonIterator.next();
			Order order = new Order();
			order.setId((Long) jsonObject.getLong("id"));
			return order.find(order.getId());  
		}

		return null;
	}
}

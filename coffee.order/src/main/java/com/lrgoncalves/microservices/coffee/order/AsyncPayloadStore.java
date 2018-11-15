package com.lrgoncalves.microservices.coffee.order;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lrgoncalves.mongodb.MongoDBInsert;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class AsyncPayloadStore {

	public static enum Type{
		REQUEST("request"),

		RESPONSE("response");

		final String type;
		Type(String type) { this.type = type; }
		public String getType() { return this.type; }
	}

	public Future<String> storePayloadAsync(Type type, final String serviceRequest) throws InterruptedException{


		CompletableFuture<String> completableFuture = new CompletableFuture<>();		

		Executors.newCachedThreadPool().submit(() -> {

			completableFuture.complete(persist(type, serviceRequest));

			return null;
		});


		return completableFuture;
	}

	private String persist(Type type, final String serviceRequest) {
		
		MongoDBInsert instance = new MongoDBInsert("order");

		BasicDBObject request = new BasicDBObject();

		request.append("type", type.getType());
		request.append("business_operation", "create_order");
		
		Date now = new Date();
		
		request.append("date", now);

		BasicDBObject payload = new BasicDBObject();
		payload.append("type", "json");

		DBObject content = null;

		if(type == Type.RESPONSE) {
			content = new BasicDBObject();
			content.put("error", (DBObject) JSON.parse(serviceRequest));
		}else {

			content = (DBObject) JSON.parse(serviceRequest);
		}

		payload.append("content", content);

		request.append("payload", payload);

		DBObject result = instance.insert(request);
		
		return result.toString();
	}

}

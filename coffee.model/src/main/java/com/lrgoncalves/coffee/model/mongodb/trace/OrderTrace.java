/**
 * 
 */
package com.lrgoncalves.coffee.model.mongodb.trace;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lrgoncalves.coffee.model.Order;

/**
 * @author lrgoncalves
 *
 */
public class OrderTrace {
	
	public static Future<String> storePayloadAsync(TraceType type, OrderBusinessOperationType orderBusinessOperationType ,final String serviceRequest) throws InterruptedException{

		CompletableFuture<String> completableFuture = new CompletableFuture<>();		

		Executors.newCachedThreadPool().submit(() -> {

			completableFuture.complete(Order.tracePersist(type, orderBusinessOperationType ,serviceRequest));

			return null;
		});

		return completableFuture;
	}
	
}

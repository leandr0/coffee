package com.lrgoncalves.coffee.model.neo4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.Drink;
import com.lrgoncalves.coffee.model.Item;
import com.lrgoncalves.coffee.model.Location;
import com.lrgoncalves.coffee.model.Milk;
import com.lrgoncalves.coffee.model.Order;
import com.lrgoncalves.coffee.model.Size;
import com.lrgoncalves.coffee.model.hateos.OrderHATEOS;
import com.lrgoncalves.coffee.model.type.DrinkType;
import com.lrgoncalves.coffee.model.type.LocationType;
import com.lrgoncalves.coffee.model.type.MilkType;
import com.lrgoncalves.coffee.model.type.SizeType;

public class AppModel {

	public static void main(String[] args) {


		try {
			//Order order = new Order();
			
			//order.findByUUDI("5031f826-7807-4912-ac80-c133d976fb44");
			
			//String link ="http://localhost:8080/microservices/order/51808860-3695-4c19-90d4-406e44912492";
			
			//System.out.println(link.replaceAll("order/.*", "order/"));
			fromObject();
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void fromObject() throws JsonProcessingException {

		List<Item> items = new LinkedList<Item>();

		Item itemA = new Item(new Size(SizeType.SMALL),new Milk( MilkType.NONE), new Drink( DrinkType.ESPRESSO));
		Item itemB = new Item(new Size(SizeType.MEDIUM),new Milk( MilkType.WHOLE), new Drink( DrinkType.ESPRESSO));
		Item itemC= new Item(new Size(SizeType.LARGE),new Milk( MilkType.SEMI), new Drink( DrinkType.CAPPUCCINO));
		Item itemD = new Item(new Size(SizeType.LARGE),new Milk( MilkType.SKIM), new Drink( DrinkType.FLAT_WHITE));
		
		items.add(itemA);
		items.add(itemB);
		items.add(itemC);
		items.add(itemD);

		Order order = new Order(new Location(LocationType.IN_STORE),items);

		/*Payment payment = new Payment();
		
		payment.setAmount(7.0);
		payment.setCardholderName("John Smith");
		payment.setCardNumber("5555 5555 5555 5555");
		payment.setExpiryMonth(10);
		payment.setExpiryYear(2022);*/
		
		ObjectMapper mapper = new ObjectMapper();

		String jsonInString = mapper.writeValueAsString(order);

		System.out.println(jsonInString);




	}

	public static void fromJson() throws JsonParseException, JsonMappingException, IOException {

		String JSON = "{\"location\":{\"type\":\"IN_STORE\"},\"items\":[{\"milk\":{\"type\":\"NONE\"},\"size\":{\"type\":\"SMALL\"},\"drink\":{\"type\":\"ESPRESSO\"}},{\"milk\":{\"type\":\"WHOLE\"},\"size\":{\"type\":\"MEDIUM\"},\"drink\":{\"type\":\"ESPRESSO\"}},{\"milk\":{\"type\":\"SEMI\"},\"size\":{\"type\":\"LARGE\"},\"drink\":{\"type\":\"CAPPUCCINO\"}},{\"milk\":{\"type\":\"SKIM\"},\"size\":{\"type\":\"LARGE\"},\"drink\":{\"type\":\"FLAT_WHITE\"}}],\"status\":{\"type\":\"UNPAID\"}}";

		ObjectMapper mapper = new ObjectMapper();

		Order order = mapper.readValue(JSON, Order.class);
		
		System.out.println(mapper.writeValueAsString(order));
		
		Order persisted = order.save(order);
		
		System.out.println(persisted.getReference().getUUDI());
		
	}

	public static void hateos() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		String JSON = "{\"location\":{\"type\":\"IN_STORE\"},\"items\":[{\"milk\":{\"type\":\"NONE\"},\"size\":{\"type\":\"SMALL\"},\"drink\":{\"type\":\"ESPRESSO\"}},{\"milk\":{\"type\":\"WHOLE\"},\"size\":{\"type\":\"MEDIUM\"},\"drink\":{\"type\":\"ESPRESSO\"}},{\"milk\":{\"type\":\"SEMI\"},\"size\":{\"type\":\"LARGE\"},\"drink\":{\"type\":\"CAPPUCCINO\"}},{\"milk\":{\"type\":\"SKIM\"},\"size\":{\"type\":\"LARGE\"},\"drink\":{\"type\":\"FLAT_WHITE\"}}],\"status\":{\"type\":\"UNPAID\"}}";

		ObjectMapper mapper = new ObjectMapper();

		Order order = mapper.readValue(JSON, Order.class);
		
		OrderHATEOS hateos = new OrderHATEOS();
		
		System.out.println(mapper.writeValueAsString(hateos.generateHATEOS(order,null)));;
	}

}

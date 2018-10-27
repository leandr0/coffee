package com.lrgoncalves.coffee.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.response.model.QueryResultModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.neo4j.Neo4jSessionFactory;
import com.lrgoncalves.coffee.model.type.DrinkType;
import com.lrgoncalves.coffee.model.type.MilkType;
import com.lrgoncalves.coffee.model.type.SizeType;


@NodeEntity(label = "ITEM")
public class Item extends NodeIdentifier implements JsonModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5037178692541974218L;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Milk milk;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Size size;

	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Drink drink;
	
	@Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
	private Order order;

	public Item(){}

	public Item(Size size, Milk milk, Drink drink) {
		this.milk = milk;
		this.size = size;
		this.drink = drink;       
	}

	public Item(SizeType size, MilkType milk, DrinkType drink) {
		this.milk = new Milk(milk);
		this.size = new Size(size);
		this.drink = new Drink(drink);       
	}

	public Item(final String size, final String milk, final String drink) {
		this.milk = new Milk(milk);
		this.size = new Size(size);
		this.drink = new Drink(drink);       
	}


	public Milk getMilk() {
		return milk;
	}

	public Size getSize() {
		return size;
	}

	public Drink getDrink() {
		return drink;
	}

	public String toString() {
		return size + " " + milk + " " + drink;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @param milk the milk to set
	 */
	public void setMilk(Milk milk) {
		this.milk = milk;
	}

	/**
	 * @param drink the drink to set
	 */
	public void setDrink(Drink drink) {
		this.drink = drink;
	}

	/**
	 * 
	 * @param item
	 * @return Item
	 */
	public Item save (Item item) {
		Neo4jSessionFactory.getInstance().getNeo4jSession().save(item);
		return find(item.getId());
	}

	/**
	 * 
	 * @param item
	 */
	public void delete(Item item) {
		Neo4jSessionFactory.getInstance().getNeo4jSession().delete(item);
	}
	
	/**
	 * 
	 * @param itemId
	 */
	public void deleteItemFull(Long itemId) {
		
		String cypher = "MATCH (i:ITEM)-[r]-(allRelatedNodes) " + 
				"WHERE ID(i) = $ITEM_ID " + 
				"AND size((allRelatedNodes)--()) = 1 " + 
				"WITH i, collect(allRelatedNodes) as allRelatedNodes " + 
				"DETACH DELETE i " + 
				"WITH allRelatedNodes " + 
				"UNWIND allRelatedNodes as node " + 
				"DELETE node"; 

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ITEM_ID", itemId);

		Neo4jSessionFactory.getInstance().getNeo4jSession().query(cypher, parameters);
				
	}
	
	/**
	 * 
	 * @param id
	 * @return Item
	 */
	public Item find(Long id) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(Item.class, id);
	}

	/**
	 * 
	 * @param id
	 * @param depth
	 * @return
	 */
	public Item find(Long id, int depth) {
		return Neo4jSessionFactory.getInstance().getNeo4jSession().load(Item.class, id, depth);
	}
	
	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public List<Item> findByOrder(final Long orderId ){

		String cypher = "MATCH (ord:ORDER)-[rel:HAS]-> (item:ITEM) WHERE ID(ord) = $ORDER_ID  RETURN item"; 

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ORDER_ID", orderId);

		QueryResultModel result = (QueryResultModel) Neo4jSessionFactory.getInstance().getNeo4jSession().query(cypher, parameters);

		List<Item> items = new LinkedList<Item>();
		
		for(Map<String,Object> map : result.queryResults()) {

			for(String set : map.keySet()) {
				items.add(find(((Item) map.get(set)).getId()));
			}
		}
		
		return items;
	}

	@Override
	public String toJson() throws JsonProcessingException {
	
		ObjectMapper mapper = new ObjectMapper();

		return mapper.writeValueAsString(this);

	}

}
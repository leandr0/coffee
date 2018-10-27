package com.lrgoncalves.coffee.model.neo4j;



import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;


public class Neo4jSessionFactory {


	private static Configuration configuration = null;

	private static SessionFactory sessionFactory = null;


	private static Neo4jSessionFactory factory = null;
	
	
	private Neo4jSessionFactory() {
	}

	public static Neo4jSessionFactory getInstance() {

		if(configuration == null) {
			configuration = new Configuration.Builder()
					.uri("bolt://localhost:7687")
					.credentials("neo4j", "!1eduarda")
					.build();
		}
		if(sessionFactory == null) {			
			sessionFactory= new SessionFactory(configuration,"com.lrgoncalves.coffee.model");
		}

		if(factory == null) {
			factory = new Neo4jSessionFactory();
		}
		
		return factory;
	}

	
	public Session getNeo4jSession() {
		return sessionFactory.openSession();
	}

}

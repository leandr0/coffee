package com.lrgoncalves.microservices.coffee.order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;


/**
 * Hello world!
 *
 */	
public class AppServer {


	private final static int port = 9080;
	private final static String host="http://localhost";

	/**
	 * 	couchdb.name=testdb
		couchdb.createdb.if-not-exist=true
		couchdb.protocol=http
		couchdb.host=127.0.0.1
		couchdb.port=5984
		couchdb.username=
		couchdb.password=
	 */

	private final static String 	COUCHDB_NAME		=	"testdb";
	private final static boolean 	COUCHDB_CREATEDB	=	true;
	private final static String 	COUCHDB_PROTOCOL	=	"http";
	private final static String 	COUCHDB_HOST		=	"localhost";
	private final static int 		COUCHDB_PORT		=	5984;
	private final static String 	COUCHDB_USERNAME	=	"";
	private final static String 	COUCHDB_PASSWD		=	"";

	public static void main( String[] args ){
		try {
			startHttpServer();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void startHttpServer() {
		//Endpoint.publish("http://localhost:8080/microservices",new OrderService());

		URI baseUri = UriBuilder.fromUri(host+":"+port+"/microservices").build();//.port(port).build();
		ResourceConfig config = new ResourceConfig(OrderService.class,ItemService.class);
		/*HttpServer server = */JdkHttpServerFactory.createHttpServer(baseUri, config);
	}

	public static void couchbd() throws IOException {

		CouchDbProperties couchDbProperties = new CouchDbProperties();

		couchDbProperties.setDbName(COUCHDB_NAME)
		.setCreateDbIfNotExist(COUCHDB_CREATEDB)
		.setProtocol(COUCHDB_PROTOCOL)
		.setHost(COUCHDB_HOST)
		.setPort(COUCHDB_PORT);
		//.setUsername(COUCHDB_USERNAME)
		//.setPassword(COUCHDB_PASSWD);

		CouchDbClient dbClient = new CouchDbClient(couchDbProperties); 
		//614d1a6a6c1942d4a51b56b695722470
		//InputStream in = new FileInputStream(new File("/root/Downloads/owl_PNG43.png"));

		//Response response = dbClient.saveAttachment(in, "owl.png","image/png");

		InputStream in = dbClient.find("614d1a6a6c1942d4a51b56b695722470/owl.png");

		//System.out.println(response.getId());

		File targetFile = new File("/root/Downloads/owl.png");
		OutputStream outStream = new FileOutputStream(targetFile);
		
		IOUtils.copy(in, outStream);

		//in.close();		

		outStream.close();

		dbClient.shutdown(); 
	}	


}

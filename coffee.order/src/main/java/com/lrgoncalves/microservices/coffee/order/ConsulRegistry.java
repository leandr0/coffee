/**
 * 
 */
package com.lrgoncalves.microservices.coffee.order;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.agent.Check;
import com.orbitz.consul.model.agent.ImmutableCheck;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.agent.Registration.RegCheck;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.ImmutableHealthCheck;

/**
 * @author lrgoncalves
 *
 */
public class ConsulRegistry {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			LinkedList<RegCheck> cheks = new LinkedList<RegCheck>();
			
			List<String> curlArgs = new LinkedList<String>();
			//curl -X GET -i http://localhost:9080/microservices/order/21b321d7-c60f-4238-be41-0a9921ca5c8d
			curlArgs.add("curl");
			curlArgs.add("-X");
			curlArgs.add("GET");
			curlArgs.add("-i");
			curlArgs.add("http://localhost:9080/microservices/order/21b321d7-c60f-4238-be41-0a9921ca5c8d");	
			
			cheks.add(Registration.RegCheck.args(curlArgs, 30, 20, "Test curl for find order service"));
			
			Consul client = Consul.builder().build();

			AgentClient agentClient = client.agentClient();

			Registration.RegCheck.http("http://localhost:9080/microservices/order/21b321d7-c60f-4238-be41-0a9921ca5c8d", 15, 5, "Test HTTP");
			
			
			RegCheck regCheck = ImmutableRegCheck.builder()//.id(UUID.randomUUID().toString())
										.args(curlArgs)
										.http("http://localhost:9080/microservices/order")
										.interval("18s")
										/*.name("Outro test")*/.build();
			
		
			
			String serviceId = UUID.randomUUID().toString();
			
			Registration service = ImmutableRegistration.builder()
									.id(serviceId)
									.name("coffee_order")
									.address("http://localhost")
									.port(9080)
									//.check(Registration.RegCheck.ttl(3L)) // registers with a TTL of 3 seconds
									//.addChecks( cheks.toArray(new RegCheck[cheks.size()]))
									.check(regCheck)
									.tags(Collections.singletonList("order"))
									.meta(Collections.singletonMap("version", "1.0"))
									.build();
			
			Optional<String> address = service.getAddress();
			
			System.out.println(address);
			
			agentClient.register(service);

			//			

			agentClient.deregister("5c09db10-9a41-4347-8bfb-69007774ba43");
			
			// Check in with Consul (serviceId required only).
			// Client will prepend "service:" for service level checks.
			// Note that you need to continually check in before the TTL expires, otherwise
			// your service's state will be marked as "critical".
			//agentClient.pass(serviceId);

			HealthClient healthClient = client.healthClient();

			System.out.println("test");
			
			ConsulResponse< ?>  responses =  healthClient.getAllServiceInstances("myService");
			
			System.out.println(responses.toString());
		
			List<HealthCheck> nodes = healthClient.getNodeChecks("lord").getResponse();

			for (HealthCheck serviceHealth : nodes) {
				System.out.println(serviceHealth.toString());
			}
			
			
			KeyValueClient kvClient = client.keyValueClient();

			kvClient.putValue("foo", "bar");
			String value = kvClient.getValueAsString("foo").get(); // bar

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}

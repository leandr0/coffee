/**
 * 
 */
package com.lrgoncalves.microservices.coffee.order;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;

/**
 * @author lrgoncalves
 *
 */
public class EurekaRegistry {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DiscoveryManager.getInstance().initComponent(new MyDataCenterInstanceConfig(), new DefaultEurekaClientConfig());

		String vipAddress = "MY-SERVICE";

		InstanceInfo nextServerInfo = null;
		try {
			nextServerInfo = DiscoveryManager.getInstance().getEurekaClient().getNextServerFromEureka(vipAddress,
					false);
		} catch (Exception e) {
			System.err.println("Cannot get an instance of example service to talk to from eureka");
			System.exit(-1);
		}

		System.out.println("Found an instance of example service to talk to from eureka: "
				+ nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());

		System.out.println("healthCheckUrl: " + nextServerInfo.getHealthCheckUrl());
		System.out.println("override: " + nextServerInfo.getOverriddenStatus());

		System.out.println("Server Host Name " + nextServerInfo.getHostName() + " at port " + nextServerInfo.getPort());

	}

}

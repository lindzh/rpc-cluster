package com.linda.framework.rpc.cluster.redis;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcRedisAdminServiceTest {
	
	public static void main(String[] args) {
		
		RedisRpcAdminService adminService = new RedisRpcAdminService();
		adminService.setRedisHost("127.0.0.1");
		adminService.setRedisPort(6379);
		adminService.startService();
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		System.out.println(JSONUtils.toJSON(rpcServers));
		for(RpcHostAndPort hap:rpcServers){
			List<RpcService> services = adminService.getRpcServices(hap);
			System.out.println(JSONUtils.toJSON(services));
		}

		HostWeight weight = new HostWeight();
		weight.setWeight(80);
		weight.setHost("172.17.9.251");
		weight.setPort(3322);
		adminService.setWeight("myapp",weight);

		List<HostWeight> weights = adminService.getWeights("myapp");

		System.out.println(JSONUtils.toJSON(weights));

		List<ConsumeRpcObject> consumers = adminService.getConsumers(RpcUtils.DEFAULT_GROUP, "com.linda.framework.rpc.cluster.LoginRpcService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers--------:"+JSONUtils.toJSON(consumers));
	}
}

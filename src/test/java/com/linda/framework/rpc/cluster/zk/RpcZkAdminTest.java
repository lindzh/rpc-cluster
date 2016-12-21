package com.linda.framework.rpc.cluster.zk;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.utils.RpcUtils;

public class RpcZkAdminTest {
	
	public static void main(String[] args) {
		ZkRpcAdminService adminService = new ZkRpcAdminService();
		adminService.setConnectString("127.0.0.1:2181");
		adminService.setNamespace("myrpc");
		adminService.startService();

		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		
		System.out.println(JSONUtils.toJSON(rpcServers));

//		setWeight(adminService);
		
		for(RpcHostAndPort server:rpcServers){
			List<RpcService> services = adminService.getRpcServices(server);
			System.out.println(JSONUtils.toJSON(server.getHost()+":"+server.getPort()+"     "+services));
		}

		List<HostWeight> weights = adminService.getWeights("myapp");
		System.out.println(JSONUtils.toJSON(weights));

		List<ConsumeRpcObject> consumers = adminService.getConsumers("hello", "com.linda.framework.rpc.cluster.HelloRpcTestService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers:"+JSONUtils.toJSON(consumers));

		consumers = adminService.getConsumers("hello", "com.linda.framework.rpc.cluster.HelloRpcService", RpcUtils.DEFAULT_VERSION);

		System.out.println("consumers:"+JSONUtils.toJSON(consumers));

		rpcServers = adminService.getRpcServers();

		System.out.println(JSONUtils.toJSON(rpcServers));

		rpcServers = adminService.getRpcServers();

		System.out.println(JSONUtils.toJSON(rpcServers));

//		adminService.stopService();
	}

	public static void setWeight(ZkRpcAdminService adminService){
		HostWeight weight = new HostWeight();
		weight.setWeight(50);
		weight.setHost("127.0.0.1");
		weight.setPort(3333);
		adminService.setWeight("myapp",weight);
	}

}

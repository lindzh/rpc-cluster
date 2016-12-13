package com.linda.framework.rpc.cluster.etcd;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.zk.ZkRpcAdminService;
import com.linda.framework.rpc.utils.RpcUtils;

public class EtcdRpcAdminTest {
	
	public static void main(String[] args) {
		
		EtcdRpcAdminService adminService = new EtcdRpcAdminService();
		adminService.setEtcdUrl("http://127.0.0.1:2379");
		adminService.setNamespace("myapp");
		
		adminService.startService();
		
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		
		System.out.println(JSONUtils.toJSON(rpcServers));

		setWeight(adminService);
		
		for(RpcHostAndPort server:rpcServers){
			List<RpcService> services = adminService.getRpcServices(server);
			System.out.println(JSONUtils.toJSON(server.getHost()+":"+server.getPort()+"     "+services));
		}

		List<HostWeight> weights = adminService.getWeights("simple");
		System.out.println(JSONUtils.toJSON(weights));

		List<ConsumeRpcObject> consumers = adminService.getConsumers(RpcUtils.DEFAULT_GROUP, "com.linda.framework.rpc.cluster.HelloRpcService", RpcUtils.DEFAULT_VERSION);
		System.out.println(JSONUtils.toJSON(consumers));
	}

	public static void setWeight(EtcdRpcAdminService adminService){
		HostWeight weight = new HostWeight();
		weight.setWeight(50);
		weight.setHost("172.17.9.251");
		weight.setPort(3353);
		adminService.setWeight("simple",weight);
	}

}

package com.linda.framework.rpc.cluster.etcd;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;

public class EtcdRpcAdminTest {
	
	public static void main(String[] args) {
		
		EtcdRpcAdminService adminService = new EtcdRpcAdminService();
		adminService.setEtcdUrl("http://192.168.139.129:2911");
		adminService.setNamespace("lindezhi");
		
		adminService.startService();
		
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		
		System.out.println(JSONUtils.toJSON(rpcServers));
		
		for(RpcHostAndPort server:rpcServers){
			List<RpcService> services = adminService.getRpcServices(server);
			System.out.println(JSONUtils.toJSON(server.getHost()+":"+server.getPort()+"     "+services));
		}
	}

}

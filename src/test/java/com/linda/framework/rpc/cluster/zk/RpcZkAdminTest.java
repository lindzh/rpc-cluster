package com.linda.framework.rpc.cluster.zk;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;

public class RpcZkAdminTest {
	
	public static void main(String[] args) {
		ZkRpcAdminService adminService = new ZkRpcAdminService();
		adminService.setConnectString("192.168.139.129:2215,192.168.139.129:2225,192.168.139.129:2235");
		adminService.setNamespace("myrpc");
		
		adminService.startService();
		
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		
		System.out.println(JSONUtils.toJSON(rpcServers));
		
		for(RpcHostAndPort server:rpcServers){
			List<RpcService> services = adminService.getRpcServices(server);
			System.out.println(JSONUtils.toJSON(server.getHost()+":"+server.getPort()+"     "+services));
		}
		
	}

}

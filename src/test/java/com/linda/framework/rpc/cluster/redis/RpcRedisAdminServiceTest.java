package com.linda.framework.rpc.cluster.redis;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;

public class RpcRedisAdminServiceTest {
	
	public static void main(String[] args) {
		
		RedisRpcAdminService adminService = new RedisRpcAdminService();
		adminService.setRedisHost("192.168.139.129");
		adminService.setRedisPort(7770);
		adminService.startService();
		List<RpcHostAndPort> rpcServers = adminService.getRpcServers();
		System.out.println(JSONUtils.toJSON(rpcServers));
		for(RpcHostAndPort hap:rpcServers){
			List<RpcService> services = adminService.getRpcServices(hap);
			System.out.println(JSONUtils.toJSON(services));
		}
	}
}

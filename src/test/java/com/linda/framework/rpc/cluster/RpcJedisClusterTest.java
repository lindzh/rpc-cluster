package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.cluster.redis.RedisRpcServer;

public class RpcJedisClusterTest {
	
	public static void main(String[] args) {
		RedisRpcServer rpcServer = new RedisRpcServer();
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(3335);
		rpcServer.setRedisHost("192.168.139.129");
		rpcServer.setRedisPort(7770);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}

package com.linda.framework.rpc.cluster.redis;

import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.HelloRpcServiceImpl;
import com.linda.framework.rpc.cluster.HelloRpcTestService;
import com.linda.framework.rpc.cluster.HelloRpcTestServiceImpl;
import com.linda.framework.rpc.cluster.LoginRpcService;
import com.linda.framework.rpc.cluster.LoginRpcServiceImpl;
import com.linda.framework.rpc.cluster.redis.RedisRpcServer;

public class RpcJedisClusterTest {
	
	public static void main(String[] args) {
		RedisRpcServer rpcServer = new RedisRpcServer();
		rpcServer.setPort(3323);
		rpcServer.setHost("127.0.0.1");
		rpcServer.setRedisHost("127.0.0.1");
		rpcServer.setRedisPort(6379);
		rpcServer.setApplication("redis-server");
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}

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
		rpcServer.setHost("127.0.0.1");
		rpcServer.setPort(3324);
		rpcServer.setRedisHost("192.168.139.129");
		rpcServer.setRedisPort(7770);
		rpcServer.register(HelloRpcService.class, new HelloRpcServiceImpl());
		rpcServer.register(LoginRpcService.class, new LoginRpcServiceImpl());
		rpcServer.register(HelloRpcTestService.class, new HelloRpcTestServiceImpl());
		rpcServer.startService();
		System.out.println("--------started----------");
	}

}

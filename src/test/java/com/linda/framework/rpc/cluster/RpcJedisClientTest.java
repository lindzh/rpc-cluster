package com.linda.framework.rpc.cluster;

import java.util.Date;

import com.linda.framework.rpc.cluster.redis.RedisRpcClient;

public class RpcJedisClientTest {

	public static void main(String[] args) throws InterruptedException {
		RedisRpcClient rpcClient = new RedisRpcClient();
		rpcClient.setRedisHost("192.168.139.129");
		rpcClient.setRedisPort(7770);
		rpcClient.startService();
		HelloRpcTestService helloRpcTestService = rpcClient.register(HelloRpcTestService.class);
		HelloRpcService helloRpcService = rpcClient.register(HelloRpcService.class);
		LoginRpcService loginRpcService = rpcClient.register(LoginRpcService.class);
		while(true){
			boolean login = loginRpcService.login("linda", "123456");
			System.out.println("login---:"+login);
			
			helloRpcService.sayHello("hihii  "+new Date(), 44);
			
			String index = helloRpcTestService.index(43, "idx--"+new Date());
			System.out.println("index:"+index);
			
			Thread.currentThread().sleep(10000L);
		}
	}
	
}

package com.linda.framework.rpc.cluster.simple;

import com.linda.framework.rpc.client.SimpleRpcClient;
import com.linda.framework.rpc.cluster.LoginRpcService;
import com.linda.framework.rpc.cluster.serializer.ProtostuffSerializer;

public class SimpleRpcClientTest {
	
	public static void main(String[] args) {
		
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4321);

		client.setSerializer(new ProtostuffSerializer());
		
		client.startService();
		
		LoginRpcService loginRpcService = client.register(LoginRpcService.class);
		
		boolean login = loginRpcService.login("linda", "123456");
		
		System.out.println("login:"+login);
		
		client.stopService();
		
	}

}

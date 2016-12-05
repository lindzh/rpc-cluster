package com.linda.framework.rpc.cluster.simple;

import com.linda.framework.rpc.client.SimpleRpcClient;
import com.linda.framework.rpc.cluster.*;
import com.linda.framework.rpc.cluster.serializer.ProtostuffSerializer;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleSerializer;

public class SimpleRpcClientTest {
	
	public static void main(String[] args) {
		
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4321);

		client.setSerializer(new SimpleSerializer());
		
		client.startService();
		
		LoginRpcService loginRpcService = client.register(LoginRpcService.class);
		
		boolean login = loginRpcService.login("linda", "123456");
		
		System.out.println("login:"+login);

		HelloRpcService helloService = client.register(HelloRpcService.class);

		TestBean tb = new TestBean();
		tb.setLimit(10);
		tb.setMessage("hahahah");
		tb.setOffset(21);
		tb.setOrder("ttith6566");

		TestRemoteBean remoteBean = helloService.getBean(tb, 100);

		System.out.println(remoteBean);

		client.stopService();
		
	}

}

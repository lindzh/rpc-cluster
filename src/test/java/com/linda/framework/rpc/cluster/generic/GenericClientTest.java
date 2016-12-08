package com.linda.framework.rpc.cluster.generic;

import java.util.HashMap;

import com.linda.framework.rpc.client.SimpleRpcClient;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleSerializer;
import com.linda.framework.rpc.generic.GenericService;
import com.linda.framework.rpc.serializer.JdkSerializer;
import com.linda.framework.rpc.utils.RpcUtils;

public class GenericClientTest {
	
	public static void main(String[] a) {
		SimpleRpcClient client = new SimpleRpcClient();
		client.setHost("127.0.0.1");
		client.setPort(4321);
		client.setSerializer(new SimpleSerializer());
		client.startService();
		GenericService service = client.register(GenericService.class);
		
		String[] getBeanTypes = new String[]{"com.linda.framework.rpc.cluster.TestBean","int"};
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("limit", 111);
		map.put("offset", 322);
		map.put("order", "trtr");
		map.put("message", "this is a test");
		Object[] getBeanArgs = new Object[]{map,543543};
		Object hh = service.invoke(null,"com.linda.framework.rpc.cluster.HelloRpcService", RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
		System.out.println(hh);
		
		String[] argTypes = new String[]{"java.lang.String","int"};
		Object[] args = new Object[]{"hello,this is linda",543543};
		Object invoke = service.invoke(null,"com.linda.framework.rpc.cluster.HelloRpcService", RpcUtils.DEFAULT_VERSION, "sayHello", argTypes, args);
		System.out.println("result:"+invoke);
		System.out.println("---------------");
		client.stopService();
	}

}

package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.serializer.ProtostuffSerializer;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleSerializer;

public class EtcdClientTest {
	
	public static void main(String[] args) throws InterruptedException {
		
		EtcdRpcClient client = new EtcdRpcClient();
		client.setEtcdUrl("http://127.0.0.1:2379");
		client.setNamespace("myapp");
		client.setApplication("test");
		client.setSerializer(new SimpleSerializer());
		client.startService();
		HelloRpcService rpcService = client.register(HelloRpcService.class);
		
		int index = 50000;
		
		while(true){

			try {
				rpcService.sayHello("this is rpc etcd test-"+index, index);
				index++;
				Thread.sleep(1000L);
			} catch (Exception e) {
				e.printStackTrace();
				Thread.sleep(1000L);
			}
		}
	}

}

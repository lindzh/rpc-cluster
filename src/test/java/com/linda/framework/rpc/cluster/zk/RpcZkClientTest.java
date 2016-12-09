package com.linda.framework.rpc.cluster.zk;

import com.linda.framework.rpc.cluster.HelloRpcService;

public class RpcZkClientTest {
	
	public static void main(String[] args) {
		ZkRpcClient client = new ZkRpcClient();
		client.setConnectString("127.0.0.1:2181");
		client.setNamespace("myrpc");
		client.startService();
		client.setApplication("test");
		HelloRpcService rpcService = client.register(HelloRpcService.class,null,"test");
		
		int index = 50000;
		
		while(true){
			rpcService.sayHello("this is rpc etcd test-"+index, index);
			index++;
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}

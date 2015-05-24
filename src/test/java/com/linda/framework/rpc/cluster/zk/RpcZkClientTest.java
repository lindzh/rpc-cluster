package com.linda.framework.rpc.cluster.zk;

import com.linda.framework.rpc.cluster.HelloRpcService;

public class RpcZkClientTest {
	
	public static void main(String[] args) {
		ZkRpcClient client = new ZkRpcClient();
		client.setConnectString("192.168.139.129:2215,192.168.139.129:2225,192.168.139.129:2235");
		client.setNamespace("myrpc");
		client.startService();
		HelloRpcService rpcService = client.register(HelloRpcService.class);
		
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

package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.HelloRpcService;

public class EtcdClientTest {
	
	public static void main(String[] args) {
		
		EtcdRpcClient client = new EtcdRpcClient();
		client.setEtcdUrl("http://192.168.139.129:2911");
		client.setNamespace("lindezhi");
		client.startService();
		HelloRpcService rpcService = client.register(HelloRpcService.class);
		rpcService.sayHello("this is rpc etcd cluster", 32323);
	}

}

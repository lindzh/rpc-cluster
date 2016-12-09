package com.linda.framework.rpc.cluster.simple;

import com.linda.framework.rpc.client.SimpleRpcClient;
import com.linda.framework.rpc.cluster.HelloRpcService;

/**
 * Created by lin on 2016/12/9.
 */
public class SimpleRpcTest {

    public static void main(String[] args) {
        SimpleRpcClient client = new SimpleRpcClient();
        client.setHost("127.0.0.1");
        client.setPort(3333);
        client.setApplication("test");

        HelloRpcService hello = client.register(HelloRpcService.class,null,"hello");

        client.startService();

        hello.sayHello("664567",66);

        String hello1 = hello.getHello();

        System.out.println("hahahah----------");
    }
}

package com.linda.framework.rpc.cluster.etcd;

import com.linda.jetcd.EtcdClient;

/**
 * Created by lin on 2016/12/12.
 */
public class Etcdtest {
    public static void main(String[] args) {
        EtcdClient client = new EtcdClient("http://127.0.0.1:2379");

        client.start();

        client.set("/myapp/weight/simple/weights/172.17.9.251:3351","50");

        client.set("/myapp/weight/simple/node","455555");
    }
}

package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.RpcClusterClient;

public class EtcdRpcClient extends RpcClusterClient{

	private String etcdUrl;

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

}

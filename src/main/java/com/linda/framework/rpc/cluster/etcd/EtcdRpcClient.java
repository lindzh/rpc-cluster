package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.cluster.RpcClusterClient;

public class EtcdRpcClient extends RpcClusterClient {

	private String etcdUrl;

	private String namespace;
	
	private EtcdRpcClientExecutor executor;

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	private void checkExecutor(){
		if(executor==null){
			executor = new EtcdRpcClientExecutor();
			executor.setEtcdUrl(etcdUrl);
			if(namespace!=null){
				executor.setNamespace(namespace);
			}
		}
	}
	
	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		this.checkExecutor();
		return executor;
	}
}

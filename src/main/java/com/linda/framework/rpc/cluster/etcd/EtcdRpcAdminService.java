package com.linda.framework.rpc.cluster.etcd;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.framework.rpc.net.AbstractRpcConnector;

public class EtcdRpcAdminService implements RpcAdminService, Service {

	private EtcdRpcClient etcdRpcClient = new EtcdRpcClient();

	public String getNamespace() {
		return etcdRpcClient.getNamespace();
	}

	public void setNamespace(String namespace) {
		etcdRpcClient.setNamespace(namespace);
	}

	public String getEtcdUrl() {
		return etcdRpcClient.getEtcdUrl();
	}

	public void setEtcdUrl(String etcdUrl) {
		etcdRpcClient.setEtcdUrl(etcdUrl);
	}

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return etcdRpcClient.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		etcdRpcClient.setConnectorClass(connectorClass);
	}

	private EtcdRpcClientExecutor getExecutor() {
		AbstractClientRemoteExecutor executor = etcdRpcClient.getRemoteExecutor();
		return (EtcdRpcClientExecutor) executor;
	}

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return this.getExecutor().getHostAndPorts();
	}

	@Override
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer) {
		return this.getExecutor().getServerService(rpcServer);
	}

	@Override
	public void startService() {
		etcdRpcClient.startService();
	}

	@Override
	public void stopService() {
		etcdRpcClient.stopService();
	}
}

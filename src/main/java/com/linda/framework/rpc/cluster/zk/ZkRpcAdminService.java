package com.linda.framework.rpc.cluster.zk;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.serializer.RpcSerializer;

public class ZkRpcAdminService implements RpcAdminService, Service {

	private ZkRpcClient zkRpcClient = new ZkRpcClient();

	public String getNamespace() {
		return zkRpcClient.getNamespace();
	}

	public String getConnectString() {
		return zkRpcClient.getConnectString();
	}

	public void setNamespace(String namespace) {
		zkRpcClient.setNamespace(namespace);
	}

	public void setConnectString(String connectString) {
		zkRpcClient.setConnectString(connectString);
	}

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return zkRpcClient.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		zkRpcClient.setConnectorClass(connectorClass);
	}

	@Override
	public void startService() {
		this.zkRpcClient.startService();
	}

	@Override
	public void stopService() {
		this.zkRpcClient.stopService();
	}

	private ZkRpcClientExecutor getExecutor() {
		AbstractClientRemoteExecutor executor = zkRpcClient.getRemoteExecutor();
		return (ZkRpcClientExecutor) executor;
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
	public void setSerializer(RpcSerializer serializer) {
		zkRpcClient.setSerializer(serializer);
	}
}

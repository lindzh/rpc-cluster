package com.linda.framework.rpc.cluster.zk;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.serializer.RpcSerializer;

public class ZkRpcAdminService extends RpcAdminService implements Service  {

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
		this.getExecutor().setAdmin(true);
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

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.getExecutor().getWeights(application);
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		this.getExecutor().setWeight(application, weight);
	}

	@Override
	public List<ConsumeRpcObject> getConsumers(String group, String service, String version) {
		return this.getExecutor().getConsumeObjects(group, service, version);
	}
}

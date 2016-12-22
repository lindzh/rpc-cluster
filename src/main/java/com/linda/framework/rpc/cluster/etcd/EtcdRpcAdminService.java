package com.linda.framework.rpc.cluster.etcd;

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

public class EtcdRpcAdminService  extends RpcAdminService implements Service  {

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
		this.getExecutor().setAdmin(true);
		etcdRpcClient.startService();
	}

	@Override
	public void stopService() {
		etcdRpcClient.stopService();
	}

	@Override
	public void setSerializer(RpcSerializer serializer) {
		etcdRpcClient.setSerializer(serializer);
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

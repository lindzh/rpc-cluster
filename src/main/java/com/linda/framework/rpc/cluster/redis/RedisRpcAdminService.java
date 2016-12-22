package com.linda.framework.rpc.cluster.redis;

import java.util.List;
import java.util.Set;

import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.framework.rpc.net.AbstractRpcConnector;
import com.linda.framework.rpc.serializer.RpcSerializer;

public class RedisRpcAdminService extends RpcAdminService implements Service {

	private RedisRpcClient redisRpcClient = new RedisRpcClient();

	public Class<? extends AbstractRpcConnector> getConnectorClass() {
		return redisRpcClient.getConnectorClass();
	}

	public void setConnectorClass(Class<? extends AbstractRpcConnector> connectorClass) {
		redisRpcClient.setConnectorClass(connectorClass);
	}

	public String getRedisHost() {
		return redisRpcClient.getRedisHost();
	}

	public void setRedisHost(String host) {
		redisRpcClient.setRedisHost(host);
	}

	public int getRedisPort() {
		return redisRpcClient.getRedisPort();
	}

	public void setRedisPort(int port) {
		redisRpcClient.setRedisPort(port);
	}

	public Set<String> getRedisSentinels() {
		return redisRpcClient.getRedisSentinels();
	}

	public void setRedisSentinels(Set<String> sentinels) {
		redisRpcClient.setRedisSentinels(sentinels);
	}

	public String getRedisMasterName() {
		return redisRpcClient.getRedisMasterName();
	}

	public void setRedisMasterName(String masterName) {
		redisRpcClient.setRedisMasterName(masterName);
	}

	public GenericObjectPoolConfig getRedisPoolConfig() {
		return redisRpcClient.getRedisPoolConfig();
	}

	public void setRedisPoolConfig(GenericObjectPoolConfig poolConfig) {
		redisRpcClient.setRedisPoolConfig(poolConfig);
	}

	private RedisRpcClientExecutor getRedisExecutor() {
		return (RedisRpcClientExecutor) redisRpcClient.getRemoteExecutor();
	}

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return getRedisExecutor().getHostAndPorts();
	}

	@Override
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer) {
		return getRedisExecutor().getServerService(rpcServer);
	}

	@Override
	public void startService() {
		this.getRedisExecutor().setAdmin(true);
		redisRpcClient.startService();
	}

	@Override
	public void stopService() {
		redisRpcClient.startService();
	}

	@Override
	public String getNamespace() {
		return redisRpcClient.getNamespace();
	}

	@Override
	public void setNamespace(String namespace) {
		redisRpcClient.setNamespace(namespace);
	}

	@Override
	public void setSerializer(RpcSerializer serializer) {
		redisRpcClient.setSerializer(serializer);		
	}

	@Override
	public List<HostWeight> getWeights(String application) {
		return this.getRedisExecutor().getWeights(application);
	}

	@Override
	public void setWeight(String application, HostWeight weight) {
		this.getRedisExecutor().setWeight(application, weight);
	}

	@Override
	public List<ConsumeRpcObject> getConsumers(String group, String service, String version) {
		return this.getRedisExecutor().getConsumeObjects(group, service, version);
	}
}

package com.linda.framework.rpc.cluster.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.linda.framework.rpc.client.AbstractClientRemoteExecutor;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcClusterClient;
import com.linda.framework.rpc.exception.RpcException;

public class RedisRpcClient extends RpcClusterClient{

	private RpcJedisDelegatePool jedisPool;
	
	private RedisRpcClientExecutor executor;
	
	public RedisRpcClient(){
		jedisPool = new RpcJedisDelegatePool();
	}
	
	@Override
	public void setRemoteExecutor(AbstractRpcClusterClientExecutor executor) {
		if(executor instanceof RedisRpcClientExecutor){
			this.executor = (RedisRpcClientExecutor)executor;
			super.setRemoteExecutor(executor);
		}else{
			throw new RpcException("jedis not supported executor:"+executor.getClass());
		}
	}

	@Override
	public AbstractClientRemoteExecutor getRemoteExecutor() {
		if(executor==null){
			executor = new RedisRpcClientExecutor();
			executor.setJedisPool(jedisPool);
		}
		return executor;
	}

	public String getHost() {
		return jedisPool.getHost();
	}

	public void setHost(String host) {
		jedisPool.setHost(host);
	}

	public int getPort() {
		return jedisPool.getPort();
	}

	public void setPort(int port) {
		jedisPool.setPort(port);
	}

	public Set<String> getSentinels() {
		return jedisPool.getSentinels();
	}

	public void setSentinels(Set<String> sentinels) {
		jedisPool.setSentinels(sentinels);
	}

	public String getMasterName() {
		return jedisPool.getMasterName();
	}

	public void setMasterName(String masterName) {
		jedisPool.setMasterName(masterName);
	}

	public GenericObjectPoolConfig getPoolConfig() {
		return jedisPool.getPoolConfig();
	}

	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		jedisPool.setPoolConfig(poolConfig);
	}
}

package com.linda.framework.rpc.cluster.redis;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * rpc 集群 redis通知
 */
public class RedisRpcClientExecutor extends AbstractRpcClusterClientExecutor implements Runnable{

	private RpcJedisDelegatePool jedisPool;
	
	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return null;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		return null;
	}

	@Override
	public void startRpcCluster() {
		jedisPool.startService();
	}

	@Override
	public void stopRpcCluster() {
		jedisPool.stopService();
	}

	@Override
	public String hash(List<String> servers) {
		return null;
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		
	}

	//定时任务，检查
	@Override
	public void run() {
		
	}

}

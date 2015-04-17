package com.linda.framework.rpc.cluster.redis;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.net.RpcNetBase;

public class RedisRpcClientExecutor extends AbstractRpcClusterClientExecutor{

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
		
	}

	@Override
	public void stopRpcCluster() {
		
	}

	@Override
	public String hash(List<String> servers) {
		return null;
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		
	}

}

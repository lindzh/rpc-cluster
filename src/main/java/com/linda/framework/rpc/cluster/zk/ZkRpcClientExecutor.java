package com.linda.framework.rpc.cluster.zk;

import java.util.List;

import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.ServiceAndVersion;
import com.linda.framework.rpc.net.RpcNetBase;

public class ZkRpcClientExecutor extends AbstractRpcClusterClientExecutor{

	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return null;
	}

	@Override
	public List<ServiceAndVersion> getServerService(RpcHostAndPort hostAndPort) {
		return null;
	}

	@Override
	public void startRpcCluster() {
		
	}

	@Override
	public void stopRpcCluster() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String hash(List<String> servers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		// TODO Auto-generated method stub
		
	}

}

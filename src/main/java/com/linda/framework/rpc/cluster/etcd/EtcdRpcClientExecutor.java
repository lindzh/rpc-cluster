package com.linda.framework.rpc.cluster.etcd;

import java.util.List;

import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.ServiceAndVersion;
import com.linda.framework.rpc.net.RpcNetBase;

public class EtcdRpcClientExecutor extends AbstractRpcClusterClientExecutor{

	@Override
	public void onStart(RpcNetBase network) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServiceAndVersion> getServerService(RpcHostAndPort hostAndPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startRpcCluster() {
		// TODO Auto-generated method stub
		
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

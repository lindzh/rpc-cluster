package com.linda.framework.rpc.cluster.etcd;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;

public class EtcdRpcAdminService implements RpcAdminService{

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return null;
	}

	@Override
	public List<RpcService> getRpcServices(String rpcServer) {
		return null;
	}

}

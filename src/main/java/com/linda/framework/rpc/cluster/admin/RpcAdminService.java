package com.linda.framework.rpc.cluster.admin;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.RpcHostAndPort;

public interface RpcAdminService{
	
	public List<RpcHostAndPort> getRpcServers();
	
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer);

}

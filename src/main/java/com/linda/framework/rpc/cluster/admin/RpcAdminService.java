package com.linda.framework.rpc.cluster.admin;

import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.serializer.RpcSerializer;

public interface RpcAdminService extends Service{
	
	public List<RpcHostAndPort> getRpcServers();
	
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer);
	
	public String getNamespace();
	
	public void setNamespace(String namespace);
	
	public void setSerializer(RpcSerializer serializer);

}

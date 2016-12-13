package com.linda.framework.rpc.cluster.admin;

import java.util.ArrayList;
import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.serializer.RpcSerializer;

public abstract class RpcAdminService implements Service{
	
	public abstract List<RpcHostAndPort> getRpcServers();
	
	public abstract List<RpcService> getRpcServices(RpcHostAndPort rpcServer);
	
	public abstract String getNamespace();
	
	public abstract void setNamespace(String namespace);
	
	public abstract void setSerializer(RpcSerializer serializer);

	/**
	 * 获取权重列表
	 * @param application
	 * @return
	 */
	public abstract List<HostWeight> getWeights(String application);

	/**
	 * 设置权重列表
	 * @param application
	 * @param weight
	 */
	public abstract void setWeight(String application,HostWeight weight);

	public abstract List<ConsumeRpcObject> getConsumers(String group, String service, String version);

}

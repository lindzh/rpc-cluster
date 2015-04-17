package com.linda.framework.rpc.cluster;

/**
 * 
 * @author lindezhi
 * 集群对象key
 */
public interface RpcClusterConst {
	
	public static final String RPC_REDIS_CHANNEL = "rpc_cluster_notify_channel";

	public static final String RPC_REDIS_HOSTS_KEY = "rpc_cluster_hosts";
	
	public static final String RPC_REDIS_SERVER_SERVICE_PREFIX = "rpc_cluster_node_";
	
}

package com.linda.framework.rpc.cluster.hash;

import java.util.List;

/**
 * 
 * @author lindezhi
 * 集群hashing 服务端列表支持
 */
public interface Hashing {
	
	public String hash(List<String> servers);
	
}

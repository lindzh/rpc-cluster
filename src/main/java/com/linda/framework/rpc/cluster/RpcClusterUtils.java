package com.linda.framework.rpc.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RpcClusterUtils {
	
	public static Set<String> toString(List<RpcHostAndPort> hostAndPorts){
		Set<String> set = new HashSet<String>();
		for(RpcHostAndPort hap:hostAndPorts){
			set.add(hap.toString());
		}
		return set;
	}

}

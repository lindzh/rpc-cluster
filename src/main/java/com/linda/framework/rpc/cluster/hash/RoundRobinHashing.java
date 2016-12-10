package com.linda.framework.rpc.cluster.hash;

import com.linda.framework.rpc.cluster.RpcHostAndPort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author lindezhi
 * 随机hashing
 */
public class RoundRobinHashing implements Hashing{
	
	private AtomicInteger index = new AtomicInteger();

	@Override
	public String hash(List<RpcHostAndPort> servers) {
		int size = servers.size();
		int idx = index.incrementAndGet()%size;
		System.out.println("select:"+servers.get(idx));
		return servers.get(0).toString();
	}

}

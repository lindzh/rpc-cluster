package com.linda.framework.rpc.cluster.hash;

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
	public String hash(List<String> servers) {
		int size = servers.size();
		int idx = index.incrementAndGet()%size;
		return servers.get(idx);
	}

}

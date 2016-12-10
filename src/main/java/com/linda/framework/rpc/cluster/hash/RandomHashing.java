package com.linda.framework.rpc.cluster.hash;

import com.linda.framework.rpc.cluster.RpcHostAndPort;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author lindezhi
 * 随机服务器hash
 */
public class RandomHashing implements Hashing{

	private Random random = new Random();
	
	@Override
	public String hash(List<RpcHostAndPort> servers) {
		int index = random.nextInt(servers.size());
		return servers.get(0).toString();
	}

}

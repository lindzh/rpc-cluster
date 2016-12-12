package com.linda.framework.rpc.cluster.hash;

import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.exception.RpcException;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author lindezhi
 * 随机服务器hash
 */
public class RandomHashing extends Hashing{

	private Random random = new Random();
	
	@Override
	public String doHash(List<RpcHostAndPort> servers) {
		int sum = 0;
		for(RpcHostAndPort server:servers){
			sum += server.getWeight();
		}

		int idx = random.nextInt(sum);

		for(RpcHostAndPort server:servers){
			idx = idx-server.getWeight();
			if(idx<0){
				return server.toString();
			}
		}
		throw new RpcException("no provider use random hash do hash");
	}

}

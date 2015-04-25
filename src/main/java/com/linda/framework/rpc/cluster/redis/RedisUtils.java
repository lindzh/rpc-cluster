package com.linda.framework.rpc.cluster.redis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;

public class RedisUtils {
	
	private static Logger logger = Logger.getLogger(RedisUtils.class);
	
	public static Object executeRedisCommand(RpcJedisDelegatePool jedisPool,JedisCallback call){
		try{
			Jedis jedis = jedisPool.getResource();
			try{
				Object result = call.callback(jedis);
				jedisPool.returnResource(jedis);
				return result;
			}catch(Exception e){
				logger.error("call error",e);
				jedisPool.returnBrokenResource(jedis);
				return null;
			}
		}catch(Exception e){
			logger.error("redis get resource error ",e);
		}
		return null;
	}
	
	public static String genServicesKey(AbstractRpcNetworkBase network){
		return RpcClusterConst.RPC_REDIS_SERVER_SERVICE_PREFIX+network.getHost()+":"+network.getPort();
	}
}

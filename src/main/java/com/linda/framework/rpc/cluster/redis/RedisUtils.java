package com.linda.framework.rpc.cluster.redis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

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
	
	public static void main(String[] args) {
		RpcJedisDelegatePool pool = new RpcJedisDelegatePool();
		Jedis jedis = pool.getResource();
		jedis.subscribe(new JedisPubSub(){

			@Override
			public void onMessage(String channel, String message) {
				
			}

			@Override
			public void onPMessage(String pattern, String channel,
					String message) {
				
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
				
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
				
			}
		}, "");
	}
}

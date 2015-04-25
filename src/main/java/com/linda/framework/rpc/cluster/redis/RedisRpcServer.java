package com.linda.framework.rpc.cluster.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.cluster.RpcMessage;
import com.linda.framework.rpc.cluster.hash.Hashing;
import com.linda.framework.rpc.cluster.hash.RoundRobinHashing;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 
 * @author lindezhi
 * 利用redis publish channel + ttl 实现server列表动态变化
 * 配置管理与通知
 */
public class RedisRpcServer extends RpcClusterServer{
	
	private RpcJedisDelegatePool jedisPool;
	
	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();
	
	private RpcNetBase network;
	
	private Timer timer = new Timer();

	private long notifyTtl = 5000;//默认5秒发送一次
	
	public RedisRpcServer(){
		this.jedisPool = new RpcJedisDelegatePool();
	}

	public String getHost() {
		return jedisPool.getHost();
	}

	public void setHost(String host) {
		jedisPool.setHost(host);
	}

	public int getPort() {
		return jedisPool.getPort();
	}

	public void setPort(int port) {
		jedisPool.setPort(port);
	}

	public Set<String> getSentinels() {
		return jedisPool.getSentinels();
	}

	public void setSentinels(Set<String> sentinels) {
		jedisPool.setSentinels(sentinels);
	}

	public String getMasterName() {
		return jedisPool.getMasterName();
	}

	public void setMasterName(String masterName) {
		jedisPool.setMasterName(masterName);
	}

	public GenericObjectPoolConfig getPoolConfig() {
		return jedisPool.getPoolConfig();
	}

	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		jedisPool.setPoolConfig(poolConfig);
	}

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		jedisPool.stopService();
		RedisRpcServer.this.notifyRpcServer(network, RpcClusterConst.CODE_SERVER_STOP);
		this.setServerStop();
		this.stopHeartBeat();
	}
	
	@Override
	public void onStart(RpcNetBase network) {
		this.startJedisAndAddHost(network);
		this.checkAndAddRpcService(network);
		this.notifyRpcServer(network,RpcClusterConst.CODE_SERVER_START);
		this.network = network;
		this.startHeartBeat();
	}
	
	private void stopHeartBeat(){
		timer.cancel();
		timer = null;
	}

	private void startHeartBeat(){
		Date start = new Date(System.currentTimeMillis()+1000L);
		timer.scheduleAtFixedRate(new HeartBeatTask(), start, notifyTtl);
	}
	
	private void setServerStop(){
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				//删除服务列表
				final String key = RedisUtils.genServicesKey(network);
				jedis.del(key);
				final HostAndPort andPort = new HostAndPort(network.getHost(), network.getPort());
				final String json = JSONUtils.toJSON(andPort);
				//删除host
				jedis.srem(RpcClusterConst.RPC_REDIS_HOSTS_KEY, json);
				return null;
			}
		});
	}
	
	private <T> void publish(RpcMessage<T> message){
		final String json = JSONUtils.toJSON(message);
		RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
			public Object callback(Jedis jedis) {
				String servicesKey = RedisUtils.genServicesKey(network);
				//默认三倍通知的过期时间
				int expire = (int)(notifyTtl*3)/1000;
				jedis.expire(servicesKey, expire);
				jedis.publish(RpcClusterConst.RPC_REDIS_CHANNEL, json);
				return null;
			}
		});
	}
	
	private void notifyRpcServer(RpcNetBase network,int messageType){
		HostAndPort andPort = new HostAndPort(network.getHost(), network.getPort());
		RpcMessage<HostAndPort> rpcMessage = new RpcMessage<HostAndPort>(messageType,andPort);
		this.publish(rpcMessage);
	}
	
	private void startJedisAndAddHost(RpcNetBase network){
		jedisPool.startService();
		final HostAndPort andPort = new HostAndPort(network.getHost(), network.getPort());
		final String json = JSONUtils.toJSON(andPort);
		RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
			public Object callback(Jedis jedis) {
				jedis.sadd(RpcClusterConst.RPC_REDIS_HOSTS_KEY, json);
				return null;
			}
		});
	}
	
	private void checkAndAddRpcService(final RpcNetBase network){
		if(rpcServiceCache.size()>0){
			RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
				public Object callback(Jedis jedis) {
					String servicesKey = RedisUtils.genServicesKey(network);
					jedis.del(servicesKey);
					for(RpcService service:rpcServiceCache){
						RedisRpcServer.this.addRpcServiceTo(jedis,service,network);
					}
					return null;
				}
			});
		}
	}
	
	private void addRpcServiceTo(Jedis jedis,RpcService service,RpcNetBase network){
		if(network!=null){
			final String key = RedisUtils.genServicesKey(network);
			final String rpcService = JSONUtils.toJSON(service);
			if(jedis!=null){
				jedis.sadd(key,rpcService);
			}else{
				RedisUtils.executeRedisCommand(jedisPool,new JedisCallback(){
					public Object callback(Jedis jedis) {
						jedis.sadd(key,rpcService);
						return null;
					}
				});
			}
		}
	}
	
	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl) {
		this.doRegister(clazz, ifaceImpl, RpcUtils.DEFAULT_VERSION);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version) {
		RpcService service = new RpcService(clazz.getName(),version,ifaceImpl.getClass().getName());
		if(this.network!=null){
			this.rpcServiceCache.add(service);
			this.addRpcServiceTo(null,service, network);
		}else{
			this.rpcServiceCache.add(service);
		}
	}
	
	private class HeartBeatTask extends TimerTask{
		@Override
		public void run() {
			RedisRpcServer.this.notifyRpcServer(network, RpcClusterConst.CODE_SERVER_HEART);
		}
	}
}

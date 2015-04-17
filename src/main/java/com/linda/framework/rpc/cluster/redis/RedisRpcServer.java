package com.linda.framework.rpc.cluster.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.cluster.serialize.RpcSerializer;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;

/**
 * 
 * @author lindezhi
 * 利用redis publish channel + ttl 实现server列表动态变化
 * 配置管理与通知
 */
public class RedisRpcServer extends RpcClusterServer implements Runnable{
	
	private RpcJedisDelegatePool jedisPool;
	
	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();
	
	private RpcNetBase network;

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		jedisPool.stopService();
	}

	@Override
	public void onStart(RpcNetBase network) {
		this.startJedisAndAddHost(network);
		this.checkAndAddRpcService(network);
		this.notifyRpcServerStart(network);
		this.network = network;
	}
	
	private void notifyRpcServerStart(RpcNetBase network){
		Jedis jedis = jedisPool.getResource();
		jedis.publish(RpcClusterConst.RPC_REDIS_CHANNEL, "");
	}
	
	private void startJedisAndAddHost(RpcNetBase network){
		jedisPool.startService();
		Jedis jedis = jedisPool.getResource();
		String hostAndPort = RpcSerializer.serializeHostAndPort(network);
		jedis.sadd(RpcClusterConst.RPC_REDIS_HOSTS_KEY, hostAndPort);
		jedisPool.returnResource(jedis);
	}
	
	private void checkAndAddRpcService(RpcNetBase network){
		if(rpcServiceCache.size()>0){
			Jedis jedis = jedisPool.getResource();
			String servicesKey = this.genServicesKey(network);
			jedis.del(servicesKey);
			for(RpcService service:rpcServiceCache){
				this.addRpcServiceTo(jedis,service,network);
			}
			jedisPool.returnResource(jedis);
		}
	}
	
	private void addRpcServiceTo(Jedis jedis,RpcService service,RpcNetBase network){
		if(jedis!=null){
			jedis = jedisPool.getResource();
		}
		String key = this.genServicesKey(network);
		jedis.sadd(key, RpcSerializer.serializeRpcService(service));
	}
	
	private String genServicesKey(RpcNetBase network){
		return RpcClusterConst.RPC_REDIS_SERVER_SERVICE_PREFIX+network.getHost()+":"+network.getPort();
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

	//定时任务延长ttl
	@Override
	public void run() {
		
	}
}

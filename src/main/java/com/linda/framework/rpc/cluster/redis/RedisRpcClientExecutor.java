package com.linda.framework.rpc.cluster.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.MessageListener;
import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.RpcMessage;
import com.linda.framework.rpc.cluster.hash.Hashing;
import com.linda.framework.rpc.cluster.hash.RoundRobinHashing;
import com.linda.framework.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * rpc 集群 redis通知
 */
public class RedisRpcClientExecutor extends AbstractRpcClusterClientExecutor implements MessageListener{
	
	private String namespace = "default";

	private RpcJedisDelegatePool jedisPool;
	
	private Timer timer = new Timer();
	
	private long checkTtl = 8000;
	
	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();
	
	private Set<String> serverMd5s = new HashSet<String>();
	
	private Map<String,List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();
	
	private Map<String,Long> heartBeanTimeCache = new ConcurrentHashMap<String,Long>();
	
	private SimpleJedisPubListener pubsubListener = new SimpleJedisPubListener();
	
	private Hashing hashing = new RoundRobinHashing();
	
	private Logger logger = Logger.getLogger(RedisRpcClientExecutor.class);
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public RpcJedisDelegatePool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(RpcJedisDelegatePool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return rpcServersCache;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		if(hostAndPort!=null){
			String key = hostAndPort.toString();
			return rpcServiceCache.get(key);
		}
		return null;
	}

	@Override
	public void startRpcCluster() {
		jedisPool.startService();
		this.startPubsubListener();
		this.startHeartBeat();
		this.fetchRpcServers();
		this.fetchRpcServices();
	}
	
	private void startPubsubListener(){
		pubsubListener.addListener(this);
		Jedis jedis = jedisPool.getResource();
		pubsubListener.setChannel(namespace+"_"+RpcClusterConst.RPC_REDIS_CHANNEL);
		pubsubListener.setJedis(jedis);
		pubsubListener.startService();
	}

	@Override
	public void stopRpcCluster() {
		this.stopHeartBeat();
		jedisPool.stopService();
		rpcServersCache = null;
		rpcServiceCache.clear();
		heartBeanTimeCache.clear();
	}

	@Override
	public String hash(List<String> servers) {
		return hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}
	
	private void closeServer(RpcHostAndPort hostAndPort){
		rpcServiceCache.remove(hostAndPort.toString());
		heartBeanTimeCache.remove(hostAndPort.toString());
		this.removeServer(hostAndPort);
	}
	
	private void removeServer(RpcHostAndPort hostAndPort){
		logger.info("removeServer "+hostAndPort.toString());
		super.removeServer(hostAndPort.toString());
		String hostAndPortStr = hostAndPort.toString();
		List<RpcHostAndPort> hostAndPorts = new ArrayList<RpcHostAndPort>();
		Set<String> newMd5s = new HashSet<String>();
		for(RpcHostAndPort hap:rpcServersCache){
			if(!hap.toString().equals(hostAndPortStr)){
				hostAndPorts.add(hap);
				String str = hap.getHost() + "_" + hap.getPort();
				String serverMd5 = MD5Utils.md5(str);
				newMd5s.add(serverMd5);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
			serverMd5s = newMd5s;
		}
	}
	
	private void fetchRpcServers(){
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				List<RpcHostAndPort> rpcServers = new ArrayList<RpcHostAndPort>();
				if(rpcServers!=null){
					Set<String> servers = jedis.smembers(namespace+"_"+RpcClusterConst.RPC_REDIS_HOSTS_KEY);
					if(servers!=null){
						serverMd5s = servers;
						for(String server:serverMd5s){
							String serverJson = jedis.get(namespace+"_"+server);
							if(serverJson!=null){
								RpcHostAndPort rpcHostAndPort = JSONUtils.fromJSON(serverJson, RpcHostAndPort.class);
								rpcServers.add(rpcHostAndPort);
							}
						}
					}
				}
				synchronized (RedisRpcClientExecutor.this) {
					rpcServersCache = rpcServers;
				}
				return null;
			}
		});
	}
	
	private void fetchRpcServices(){
		for(RpcHostAndPort hostAndPort:rpcServersCache){
			this.fetchRpcServices(hostAndPort);
		}
	}
	
	private void fetchRpcServices(final RpcHostAndPort hostAndPort){
		String str = hostAndPort.getHost() + "_" + hostAndPort.getPort();
		String serverMd5 = MD5Utils.md5(str);
		final String servicesKey = RedisUtils.genServicesKey(namespace, serverMd5);
		RedisUtils.executeRedisCommand(jedisPool, new JedisCallback(){
			public Object callback(Jedis jedis) {
				List<RpcService> rpcServices = new ArrayList<RpcService>();
				Set<String> services = jedis.smembers(servicesKey);
				if(services!=null){
					for(String service:services){
						RpcService rpcService = JSONUtils.fromJSON(service, RpcService.class);
						rpcServices.add(rpcService);
					}
				}
				rpcServiceCache.put(hostAndPort.toString(), rpcServices);
				return null;
			}
		});
	}

	@Override
	public void onMessage(RpcMessage message) {
		logger.info("onMessage:"+JSONUtils.toJSON(message));
		RpcHostAndPort hostAndPort = (RpcHostAndPort)message.getMessage();
		int messageType = message.getMessageType();
		if(messageType==RpcClusterConst.CODE_SERVER_STOP){
			this.closeServer(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_HEART){
			this.serverAddOrHearBeat(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_START){
			this.serverAddOrHearBeat(hostAndPort);
		}else if(messageType==RpcClusterConst.CODE_SERVER_ADD_RPC){
			this.fetchRpcServices(hostAndPort);
		}
	}
	
	private void serverAddOrHearBeat(RpcHostAndPort hostAndPort){
		Long time = heartBeanTimeCache.get(hostAndPort.toString());
		if(time!=null){
			long now = System.currentTimeMillis();
			if(now-time<checkTtl){
				heartBeanTimeCache.put(hostAndPort.toString(), System.currentTimeMillis());
				return;
			}
		}
		this.fetchRpcServers();
		heartBeanTimeCache.put(hostAndPort.toString(), System.currentTimeMillis());
		this.fetchRpcServices(hostAndPort);
		//动态更新集群
		this.startConnector(hostAndPort);
	}
	
	private void stopHeartBeat(){
		timer.cancel();
	}
	
	/**
	 * 启动心跳定时检测
	 */
	private void startHeartBeat(){
		timer.scheduleAtFixedRate(new HeartBeatTask(), checkTtl, checkTtl);
	}
	
	private void checkHeartBeat(){
		List<RpcHostAndPort> needRemoveServers = new ArrayList<RpcHostAndPort>();
		List<RpcHostAndPort> rpcServers = new ArrayList<RpcHostAndPort>(Arrays.asList(new RpcHostAndPort[rpcServersCache.size()]));
		Collections.copy(rpcServers, rpcServersCache);
		for(RpcHostAndPort server:rpcServers){
			Long beat = heartBeanTimeCache.get(server.toString());
			if(beat==null){
				needRemoveServers.add(server);
			}else{
				long now = System.currentTimeMillis();
				if(now-beat>checkTtl){
					needRemoveServers.add(server);
				}
			}
		}
		for(RpcHostAndPort removeServer:needRemoveServers){
			this.removeServer(removeServer);
		}
	}
	
	private class HeartBeatTask extends TimerTask{
		@Override
		public void run() {
			checkHeartBeat();
		}
	}
}

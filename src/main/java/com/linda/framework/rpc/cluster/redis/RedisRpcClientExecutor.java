package com.linda.framework.rpc.cluster.redis;

import java.util.List;
import java.util.TimerTask;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPubSub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MessageListener;
import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.RpcMessage;
import com.linda.framework.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * rpc 集群 redis通知
 */
public class RedisRpcClientExecutor extends AbstractRpcClusterClientExecutor implements MessageListener{

	private RpcJedisDelegatePool jedisPool;
	
	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return null;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		return null;
	}

	@Override
	public void startRpcCluster() {
		jedisPool.startService();
	}

	@Override
	public void stopRpcCluster() {
		jedisPool.stopService();
	}

	@Override
	public String hash(List<String> servers) {
		return null;
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		
	}

	@Override
	public void onMessage(RpcMessage message) {
		
	}
	
	private class HeartBeatTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class JedisMessageListener extends JedisPubSub{
		
		private MessageListener listener;
		
		public JedisMessageListener(MessageListener listener){
			this.listener = listener;
		}
		
		@Override
		public void onMessage(String channel, String message) {
			if(channel.equals(RpcClusterConst.RPC_REDIS_CHANNEL)){
				RpcMessage<HostAndPort> rpcMessage = JSONUtils.fromJSON(message, new TypeReference<RpcMessage<HostAndPort>>(){});
				if(this.listener!=null){
					this.listener.onMessage(rpcMessage);
				}
			}
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
			
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
	}
}

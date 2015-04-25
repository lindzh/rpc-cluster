package com.linda.framework.rpc.cluster.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPubSub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MessageListener;
import com.linda.framework.rpc.cluster.RpcClusterConst;
import com.linda.framework.rpc.cluster.RpcMessage;

/**
 * redis pub sub 接收集群消息心跳
 * @author lindezhi
 *
 */
public class SimpleJedisPubListener extends JedisPubSub{

	private List<MessageListener> listeners = new ArrayList<MessageListener>();
	
	public void fireListeners(RpcMessage message){
		for(MessageListener listener:listeners){
			listener.onMessage(message);
		}
	}
	
	public void addListener(MessageListener listener){
		this.listeners.add(listener);
	}
	
	@Override
	public void onMessage(String channel, String message) {
		if(channel.equals(RpcClusterConst.RPC_REDIS_CHANNEL)){
			RpcMessage<HostAndPort> rpcMessage = JSONUtils.fromJSON(message, new TypeReference<RpcMessage<HostAndPort>>(){});
			this.fireListeners(rpcMessage);
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

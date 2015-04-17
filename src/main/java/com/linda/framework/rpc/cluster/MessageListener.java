package com.linda.framework.rpc.cluster;

import com.linda.framework.rpc.cluster.message.AbstractRpcMessage;

/**
 * 
 * @author lindezhi
 * 消息监听器，方便接收消息提供给上层
 */
public interface MessageListener {
	
	public void onMessage(AbstractRpcMessage message);
	
}

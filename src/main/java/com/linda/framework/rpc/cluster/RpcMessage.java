package com.linda.framework.rpc.cluster;

public abstract class RpcMessage<T> {
	
	private int messageType;
	
	private T message;

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
}

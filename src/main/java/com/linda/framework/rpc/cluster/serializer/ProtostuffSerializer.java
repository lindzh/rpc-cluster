package com.linda.framework.rpc.cluster.serializer;

import java.io.Serializable;

import com.linda.framework.rpc.cluster.ProtostuffUtils;
import com.linda.framework.rpc.serializer.RpcSerializer;

public class ProtostuffSerializer implements RpcSerializer {
	
	private static class ProtostuffObject implements Serializable{
		
		private static final long serialVersionUID = -1518242254508266543L;
		private Object refer;

	}

	@Override
	public byte[] serialize(Object obj) {
		ProtostuffObject stuff = new ProtostuffObject();
		stuff.refer = obj;
		return ProtostuffUtils.serialize(stuff);
	}

	@Override
	public Object deserialize(byte[] bytes) {
		ProtostuffObject stuff = ProtostuffUtils.deserialize(bytes, ProtostuffObject.class);
		return stuff.refer;
	}
}

package com.linda.framework.rpc.serialize;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.cluster.serializer.JSONSerializer;
import com.linda.framework.rpc.serializer.RpcSerializer;

public class JSONSerializerTest extends AbstractSerializer {
	
	public static void main(String[] args) {
		SerializeTest test = new SerializeTest();
		RemoteCall call = test.getCall();
		RpcSerializer serializer = new JSONSerializer();
		long start = System.currentTimeMillis();
		byte[] bs = serializer.serialize(call);
		long end = System.currentTimeMillis();
		long cost = end-start;
		System.out.println("json serializer length:"+bs.length+" cost:"+cost);
	}

}

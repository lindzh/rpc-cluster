package com.linda.framework.rpc.serialize;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.serializer.JdkSerializer;

public class SerializeTest extends AbstractSerializer{
	
	public static void main(String[] args) {
		SerializeTest test = new SerializeTest();
		RemoteCall call = test.getCall();
		JdkSerializer serializer = new JdkSerializer();
		long start = System.currentTimeMillis();
		byte[] bs = serializer.serialize(call);
		long end = System.currentTimeMillis();
		long cost = end-start;
		System.out.println("jdk serializer length:"+bs.length+" cost:"+cost);
	}

}

package com.linda.framework.rpc.serialize;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.cluster.HelloRpcService;
import com.linda.framework.rpc.cluster.TestBean;

public class AbstractSerializer {

	public RemoteCall getCall(){
		String service = HelloRpcService.class.getName();
		String method = "getBean";
		String version = "534543";
		TestBean testBean = new TestBean();
		testBean.setLimit(4);
		testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
		testBean.setOffset(43432);
		testBean.setOrder("645gdfghdfghdf");
		RemoteCall call = new RemoteCall(service, method);
		call.setArgs(new Object[]{testBean,654645});
		call.setVersion(version);
		return call;
	}
}

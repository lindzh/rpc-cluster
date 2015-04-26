package com.linda.framework.rpc.cluster.etcd;

import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.net.RpcNetBase;

/**
 * 
 * @author lindezhi
 * 用 coreos的etcd实现server列表与rpc服务列表动态通知与管理
 */
public class EtcdRpcServer extends RpcClusterServer{
	
	
	private String etcdUrl;

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		
	}

	@Override
	public void onStart(RpcNetBase network){
		
		
	}
	
	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl) {
		
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version) {
		
	}

}

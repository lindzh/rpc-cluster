package com.linda.framework.rpc.cluster.admin;

import java.util.ArrayList;
import java.util.List;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.client.SimpleRpcClient;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.monitor.RpcMonitorService;

public class SimpleRpcAdminService implements RpcAdminService, Service {

	private SimpleRpcClient client = new SimpleRpcClient();
	
	private RpcMonitorService monitorService;
	
	private List<RpcHostAndPort> hosts = new ArrayList<RpcHostAndPort>();

	public String getHost() {
		return client.getHost();
	}

	public void setHost(String host) {
		client.setHost(host);
	}

	public int getPort() {
		return client.getPort();
	}

	public void setPort(int port) {
		client.setPort(port);
	}

	@Override
	public void startService() {
		client.startService();
		RpcHostAndPort hostAndPort = new RpcHostAndPort();
		hostAndPort.setHost(client.getHost());
		hostAndPort.setPort(client.getPort());
		hosts.add(hostAndPort);
		monitorService = client.register(RpcMonitorService.class);
	}

	@Override
	public void stopService() {
		client.stopService();
	}

	@Override
	public List<RpcHostAndPort> getRpcServers() {
		return hosts;
	}

	@Override
	public List<RpcService> getRpcServices(RpcHostAndPort rpcServer) {
		return monitorService.getRpcServices();
	}
}

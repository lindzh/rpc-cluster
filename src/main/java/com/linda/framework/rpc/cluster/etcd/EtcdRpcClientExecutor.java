package com.linda.framework.rpc.cluster.etcd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.hash.Hashing;
import com.linda.framework.rpc.cluster.hash.RoundRobinHashing;
import com.linda.framework.rpc.net.RpcNetBase;

import com.linda.jetcd.EtcdChangeResult;
import com.linda.jetcd.EtcdClient;
import com.linda.jetcd.EtcdWatchCallback;

public class EtcdRpcClientExecutor extends AbstractRpcClusterClientExecutor implements EtcdWatchCallback{
	
	private EtcdClient etcdClient;
	
	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();
	
	private Map<String,List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();
	
	private Map<String,Long> heartBeanTimeCache = new ConcurrentHashMap<String,Long>();
	
	private Hashing hashing = new RoundRobinHashing();
	
	private Logger logger = Logger.getLogger(EtcdRpcClientExecutor.class);
	
	public EtcdRpcClientExecutor(String etcdUrl){
		etcdClient = new EtcdClient(etcdUrl);
	}

	@Override
	public void onStart(RpcNetBase network) {
		
	}

	@Override
	public List<RpcHostAndPort> getHostAndPorts() {
		return rpcServersCache;
	}

	@Override
	public List<RpcService> getServerService(RpcHostAndPort hostAndPort) {
		if(hostAndPort!=null){
			String key = hostAndPort.toString();
			return rpcServiceCache.get(key);
		}
		return null;
	}

	@Override
	public void startRpcCluster() {
		this.fetchRpcServers();
		this.fetchRpcServices();
	}
	
	@Override
	public void stopRpcCluster() {
		rpcServersCache = null;
		rpcServiceCache.clear();
		heartBeanTimeCache.clear();
	}

	@Override
	public String hash(List<String> servers) {
		return hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}
	
	private void closeServer(RpcHostAndPort hostAndPort){
		rpcServiceCache.remove(hostAndPort.toString());
		heartBeanTimeCache.remove(hostAndPort.toString());
		this.removeServer(hostAndPort);
	}
	
	private void removeServer(RpcHostAndPort hostAndPort){
		logger.info("removeServer "+hostAndPort.toString());
		super.removeServer(hostAndPort.toString());
		String hostAndPortStr = hostAndPort.toString();
		List<RpcHostAndPort> hostAndPorts = new ArrayList<RpcHostAndPort>();
		for(RpcHostAndPort hap:rpcServersCache){
			if(!hap.toString().equals(hostAndPortStr)){
				hostAndPorts.add(hap);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
		}
	}
	
	private void fetchRpcServers(){
		
		//TODO
	}
	
	private void fetchRpcServices(){
		for(RpcHostAndPort hostAndPort:rpcServersCache){
			this.fetchRpcServices(hostAndPort);
		}
	}
	
	private void fetchRpcServices(final RpcHostAndPort hostAndPort){
	}

	private void serverAddOrHearBeat(RpcHostAndPort hostAndPort){
		Long time = heartBeanTimeCache.get(hostAndPort.toString());
		this.fetchRpcServers();
		heartBeanTimeCache.put(hostAndPort.toString(), System.currentTimeMillis());
		this.fetchRpcServices(hostAndPort);
		//动态更新集群
		this.startConnector(hostAndPort);
	}
	
	@Override
	public void onChange(EtcdChangeResult future) {
		
	}
}

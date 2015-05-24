package com.linda.framework.rpc.cluster.zk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.hash.Hashing;
import com.linda.framework.rpc.cluster.hash.RoundRobinHashing;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.RpcNetBase;

public class ZkRpcClientExecutor extends AbstractRpcClusterClientExecutor{
	
	private CuratorFramework zkclient;
	
	private String namespace = "rpc";
	
	private String connectString;
	
	private int connectTimeout = 8000;
	
	private int maxRetry = 5;
	
	private int baseSleepTime = 1000;
	
	private String defaultEncoding = "utf-8";

	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();

	private Map<String, List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();

	private Hashing hashing = new RoundRobinHashing();

	private Logger logger = Logger.getLogger("rpcCluster");
	
	private CuratorWatcher providerWatcher = new CuratorWatcher(){
		@Override
		public void process(WatchedEvent event) throws Exception {
			ZkRpcClientExecutor.this.fetchRpcServers(true);
		}
	};
	
	private String genServerListKey() {
		return "/" + namespace + "/servers";
	}

	private String getServiceListKey(String serverKey) {
		return "/" + namespace + "/services/" + serverKey;
	}
	
	private String genServerKey(RpcHostAndPort hostAndPort) {
		String str = hostAndPort.getHost()+"_"+hostAndPort.getPort();
		return MD5Utils.md5(str);
	}
	
	private void initZk(){
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetry);
		zkclient = CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
		.connectionTimeoutMs(connectTimeout).sessionTimeoutMs(connectTimeout).retryPolicy(retryPolicy).build();
		zkclient.start();
	}
	
	private void handleZkException(Exception e){
		logger.error("zk error", e);
		throw new RpcException(e);
	}
	
	/**
	 * 新的server添加进来
	 * @param serverMd5
	 * @return
	 */
	private boolean isNew(String serverMd5){
		boolean isNew = true;
		for(RpcHostAndPort hap:rpcServersCache){
			String str = hap.getHost() + "_" + hap.getPort();
			String oldMd5 = MD5Utils.md5(str);
			if(oldMd5.equals(serverMd5)){
				isNew = false;
				break;
			}
		}
		return isNew;
	}
	
	/**
	 * 旧的server去掉
	 * @param pathes
	 * @param hap
	 * @return
	 */
	private boolean isOld(List<String> pathes,RpcHostAndPort hap){
		boolean isOld = true;
		String str = hap.getHost() + "_" + hap.getPort();
		String oldMd5 = MD5Utils.md5(str);
		for(String path:pathes){
			if(path.contains(oldMd5)){
				isOld = false;
			}
		}
		return isOld;
	}
	
	private void getServices(List<String> services,RpcHostAndPort hostAndPort){
		String hostAndPortString = hostAndPort.toString();
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		ArrayList<RpcService> rpcServices = new ArrayList<RpcService>();
		for(String service:services){
			String key = serviceListKey+"/"+service;
			try{
				byte[] data = this.zkclient.getData().forPath(key);
				String json = new String(data,this.defaultEncoding);
				RpcService rpcService = JSONUtils.fromJSON(json, RpcService.class);
				rpcServices.add(rpcService);
			}catch(Exception e){
				this.handleZkException(e);
			}
		}
		rpcServiceCache.put(hostAndPortString, rpcServices);
	}
	
	private void fetchServices(RpcHostAndPort hostAndPort){
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		try {
			List<String> services = this.zkclient.getChildren().forPath(serviceListKey);
			this.getServices(services, hostAndPort);
		} catch (Exception e) {
			this.handleZkException(e);
		}
	}
	
	private void addServer(RpcHostAndPort hostAndPort,boolean startConnections){
		boolean add = true;
		String server = hostAndPort.toString();
		synchronized (rpcServersCache) {
			for(RpcHostAndPort hap:rpcServersCache){
				if(hap.toString().equals(server)){
					add = false;
					break;
				}
			}
			if(add){
				rpcServersCache.add(hostAndPort);
			}
		}
		//获取服务列表
		if(add){
			this.fetchServices(hostAndPort);
		}
		//添加并启动链接
		if(add&&startConnections){
			this.startConnector(hostAndPort);
		}
	}
	
	private void removeServer0(RpcHostAndPort hostAndPort){
		String server = hostAndPort.toString();
		logger.info("removeServer " + server);
		this.rpcServiceCache.remove(server);
		super.removeServer(hostAndPort.toString());
		List<RpcHostAndPort> hostAndPorts = new ArrayList<RpcHostAndPort>();
		for (RpcHostAndPort hap : rpcServersCache) {
			if (!hap.toString().equals(server)) {
				hostAndPorts.add(hap);
			}
		}
		synchronized (this) {
			rpcServersCache = hostAndPorts;
		}
	}
	
	private void fetchServers(List<String> pathes,boolean startConnections){
		//watch for change
		try{
			this.zkclient.getChildren().usingWatcher(providerWatcher).inBackground().forPath(this.genServerListKey());
		}catch(Exception e){
			this.handleZkException(e);
		}
		if(pathes!=null){
			for(String path:pathes){
				if(this.isNew(path)){
					String key = this.genServerListKey()+"/"+path;
					try {
						byte[] data = this.zkclient.getData().forPath(key);
						String json = new String(data,this.defaultEncoding);
						RpcHostAndPort hostAndPort = JSONUtils.fromJSON(json, RpcHostAndPort.class);
						this.addServer(hostAndPort, startConnections);
					} catch (Exception e) {
						this.handleZkException(e);
					}
				}
			}
			for(RpcHostAndPort hostAndPort:rpcServersCache){
				if(this.isOld(pathes, hostAndPort)){
					this.removeServer0(hostAndPort);
				}
			}
		}
	}
	
	private void fetchRpcServers(boolean startConnectors) {
		try{
			List<String> pathes = this.zkclient.getChildren().forPath(this.genServerListKey());
			this.fetchServers(pathes,startConnectors);
		}catch(Exception e){
			this.handleZkException(e);
		}
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
		if (hostAndPort != null) {
			String key = hostAndPort.toString();
			return rpcServiceCache.get(key);
		}
		return Collections.emptyList();
	}

	@Override
	public void startRpcCluster() {
		this.initZk();
		this.fetchRpcServers(false);
	}

	@Override
	public void stopRpcCluster() {
		this.zkclient.close();
		rpcServersCache = null;
		rpcServiceCache.clear();
	}

	@Override
	public String hash(List<String> servers) {
		return this.hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}
	
	private void closeServer(RpcHostAndPort hostAndPort) {
		rpcServiceCache.remove(hostAndPort.toString());
		this.removeServer0(hostAndPort);
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public int getBaseSleepTime() {
		return baseSleepTime;
	}

	public void setBaseSleepTime(int baseSleepTime) {
		this.baseSleepTime = baseSleepTime;
	}
}

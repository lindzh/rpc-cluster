package com.linda.framework.rpc.cluster.zk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;
import org.apache.zookeeper.KeeperException;

/**
 * 基于zk的服务化管理
 * @author lindezhi
 *
 */
public class ZkRpcServer extends RpcClusterServer{
	
	private CuratorFramework zkclient;
	
	private String connectString;
	
	private String namespace = "rpc";
	
	private int connectTimeout = 8000;
	
	private int maxRetry = 5;
	
	private int baseSleepTime = 1000;
	
	private String serverMd5 = null;
	
	private RpcNetBase network;
	
	private long time = 0;
	
	private Logger logger = Logger.getLogger("rpcCluster");
	
	private String defaultEncoding = "utf-8";
	
	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();
	
	private String genServerKey() {
		return "/servers/" + this.serverMd5;
	}

	private String genServerServiceKey() {
		return "/services/" + this.serverMd5;
	}

	private String genServiceKey(String serviceMd5) {
		return "/services/" + this.serverMd5 + "/" + serviceMd5;
	}

	private Random random = new Random();
	
	@Override
	public void onClose(RpcNetBase network, Exception e) {
		if(zkclient!=null){
			this.cleanIfExist();
			this.zkclient.close();
		}
	}

	@Override
	public void onStart(RpcNetBase network) {
		time = System.currentTimeMillis();
		this.initServerMd5(network);
		this.initZk();
		this.cleanIfExist();
		this.checkAndAddRpcService();
		this.addProviderServer();
		//设置权重,默认100
		this.doSetWehgit(getApplication(),this.getHost()+":"+this.getPort(),100,false);
	}
	
	private void addProviderServer(){
		RpcHostAndPort hostAndPort = new RpcHostAndPort(network.getHost(),network.getPort());
		hostAndPort.setTime(time);
		hostAndPort.setToken(this.getToken());

		String serverKey = this.genServerKey();
		String hostAndPortJson = JSONUtils.toJSON(hostAndPort);
		logger.info("create rpc provider:"+hostAndPortJson);
		try{
			byte[] data = hostAndPortJson.getBytes(defaultEncoding);
			this.zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(serverKey, data);
			logger.info("add rpc provider success "+serverKey);
		}catch(Exception e){
			logger.error("add provider error",e);
			throw new RpcException(e);
		}
	}
	
	private void initServerMd5(RpcNetBase network){
		this.network = network;
		String str = network.getHost() + "_" + network.getPort();
		this.serverMd5 = MD5Utils.md5(str);
	}
	
	private void initZk(){
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetry);
		zkclient = CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
		.connectionTimeoutMs(connectTimeout).sessionTimeoutMs(connectTimeout).retryPolicy(retryPolicy).build();
		zkclient.start();
		logger.info("init zk connection success");
	}
	
	private void cleanIfExist() {
		try{
			// 删除server
			String serverKey = this.genServerKey();
			this.zkclient.delete().forPath(serverKey);
		}catch(Exception e){
			logger.error("add provider error",e);
		}
		try{
			// 删除server的service列表
			String serverServiceKey = this.genServerServiceKey();
			this.zkclient.delete().deletingChildrenIfNeeded().forPath(serverServiceKey);
		}catch(Exception e){
			logger.error("add provider error",e);
		}
		logger.info("clean server data");
	}
	
	private void makeSurePath(){
		String serviceKey = this.genServerServiceKey();
		try {
			this.zkclient.create().creatingParentsIfNeeded().forPath(serviceKey);
		} catch (Exception e) {
			logger.error("add provider error",e);
		}
	}
	
	private void checkAndAddRpcService() {
		this.makeSurePath();
		for (RpcService rpcService : rpcServiceCache) {
			this.addRpcService(rpcService);
		}
	}

	private void addRpcService(RpcService service) {
		String key = service.getName() + "_" + service.getVersion();
		String serviceMd5 = MD5Utils.md5(key);
		String serviceKey = this.genServiceKey(serviceMd5);
		String serviceJson = JSONUtils.toJSON(service);
		logger.info("addRpcService:"+serviceJson);
		try{
			byte[] data = serviceJson.getBytes(defaultEncoding);
			this.zkclient.create().forPath(serviceKey, data);
		}catch(Exception e){
			logger.error("add provider error",e);
			throw new RpcException(e);
		}
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl) {
		this.doRegister(clazz, ifaceImpl, RpcUtils.DEFAULT_VERSION);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version) {
		this.doRegister(clazz, ifaceImpl, version,RpcUtils.DEFAULT_GROUP);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version,String group) {
		RpcService service = new RpcService(clazz.getName(), version, ifaceImpl.getClass().getName());
		service.setTime(System.currentTimeMillis());

		service.setApplication(this.getApplication());

		service.setGroup(group);

		if (this.network != null) {
			this.rpcServiceCache.add(service);
			this.addRpcService(service);
		} else {
			this.rpcServiceCache.add(service);
		}
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
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


	private String genWeightKey(String application,String hostkey){
		return "/weights/"+application+"/"+hostkey;
	}

	private String genApplicationWeightsKey(String application){
		return "/weights/"+application;
	}

	/**
	 * 设置权重列表
	 * @param application
	 * @param key
	 * @param weight
	 * @param override
	 */
	private void doSetWehgit(String application,String key,int weight,boolean override){
		String path = this.genWeightKey(application,key);
		byte[] data = (""+weight).getBytes();
		if(override){
				try{
					zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
				}catch(Exception e){
					throw new RpcException(e);
				}

		}else{
				try{
					byte[] bytes = zkclient.getData().forPath(path);
					if(bytes==null){
						zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
					}
					return;
				}catch(Exception e){
					if(e instanceof KeeperException.NoNodeException){
						try {
							zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
						} catch (Exception e1) {
							throw new RpcException(e1);
						}
					}
				}
		}
		//通过weight app data通知
		//notify change
		String applicationWeightsKey = this.genApplicationWeightsKey(application);
		int idx = this.random.nextInt(100000000);
		byte[] appData = (application+"_"+idx).getBytes();
		try {
			zkclient.setData().forPath(applicationWeightsKey,appData);
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
}

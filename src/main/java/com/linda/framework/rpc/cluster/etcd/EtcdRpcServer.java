package com.linda.framework.rpc.cluster.etcd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.RpcClusterServer;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.framework.rpc.utils.RpcUtils;
import com.linda.jetcd.EtcdClient;

/**
 * 
 * @author lindezhi 用 coreos的etcd实现server列表与rpc服务列表动态通知与管理
 */
public class EtcdRpcServer extends RpcClusterServer {

	private EtcdClient etcdClient;

	private String etcdUrl;

	private String namespace = "rpc";

	private List<RpcService> rpcServiceCache = new ArrayList<RpcService>();

	private RpcNetBase network;

	private Timer timer = new Timer();

	private long notifyTtl = 5000;// 默认5秒发送一次

	private int serverttl = 10;// 10ms

	private String serverMd5 = null;
	
	private Logger logger = Logger.getLogger("rpcCluster");

	private String genServerKey() {
		return "/"+namespace+"/servers/" + this.serverMd5;
	}

	private String genServerServiceKey() {
		return "/"+namespace+"/services/" + this.serverMd5;
	}

	private String genServiceKey(String serviceMd5) {
		return "/"+namespace+"/services/" + this.serverMd5 + "/" + serviceMd5;
	}

	public EtcdRpcServer() {

	}

	@Override
	public void onClose(RpcNetBase network, Exception e) {
		this.cleanIfExist();
		this.stopHeartBeat();
		etcdClient.stop();
	}

	@Override
	public void onStart(RpcNetBase network) {
		etcdClient = new EtcdClient(etcdUrl);
		etcdClient.start();
		String str = network.getHost() + "_" + network.getPort();
		this.serverMd5 = MD5Utils.md5(str);
		this.network = network;
		this.checkAndAddRpcService();
		this.startHeartBeat();
	}

	private void stopHeartBeat() {
		timer.cancel();
		timer = null;
	}

	private void startHeartBeat() {
		Date start = new Date(System.currentTimeMillis() + 1000L);
		timer.scheduleAtFixedRate(new HeartBeatTask(), start, notifyTtl);
	}

	private void cleanIfExist() {
		// 删除server
		String serverKey = this.genServerKey();
		this.etcdClient.del(serverKey);
		// 删除server的service列表
		String serverServiceKey = this.genServerServiceKey();
		this.etcdClient.delDir(serverServiceKey, true);
	}

	private void checkAndAddRpcService() {
		this.cleanIfExist();
		for (RpcService rpcService : rpcServiceCache) {
			this.addRpcService(rpcService);
		}
		this.updateServerTtl();
	}

	private void addRpcService(RpcService service) {
		String key = service.getName() + "_" + service.getVersion();
		String serviceMd5 = MD5Utils.md5(key);
		String serviceKey = this.genServiceKey(serviceMd5);
		String serviceJson = JSONUtils.toJSON(service);
		logger.info("addRpcService:"+serviceJson);
		this.etcdClient.set(serviceKey, serviceJson);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl) {
		this.doRegister(clazz, ifaceImpl, RpcUtils.DEFAULT_VERSION);
	}

	@Override
	protected void doRegister(Class<?> clazz, Object ifaceImpl, String version) {
		RpcService service = new RpcService(clazz.getName(), version, ifaceImpl.getClass().getName());
		if (this.network != null) {
			this.rpcServiceCache.add(service);
			this.addRpcService(service);
		} else {
			this.rpcServiceCache.add(service);
		}
	}

	private void updateServerTtl() {
		RpcHostAndPort hostAndPort = new RpcHostAndPort(network.getHost(),network.getPort());
		String serverKey = this.genServerKey();
		String hostAndPortJson = JSONUtils.toJSON(hostAndPort);
		logger.info("updateServerTTL:"+hostAndPortJson);
		this.etcdClient.set(serverKey, hostAndPortJson, serverttl);
	}

	private class HeartBeatTask extends TimerTask {
		@Override
		public void run() {
			EtcdRpcServer.this.updateServerTtl();
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getEtcdUrl() {
		return etcdUrl;
	}

	public void setEtcdUrl(String etcdUrl) {
		this.etcdUrl = etcdUrl;
	}

}

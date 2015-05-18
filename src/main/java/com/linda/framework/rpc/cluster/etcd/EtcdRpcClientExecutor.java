package com.linda.framework.rpc.cluster.etcd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.AbstractRpcClusterClientExecutor;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.RpcClusterUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.hash.Hashing;
import com.linda.framework.rpc.cluster.hash.RoundRobinHashing;
import com.linda.framework.rpc.net.RpcNetBase;
import com.linda.jetcd.EtcdChangeResult;
import com.linda.jetcd.EtcdClient;
import com.linda.jetcd.EtcdNode;
import com.linda.jetcd.EtcdResult;
import com.linda.jetcd.EtcdWatchCallback;

public class EtcdRpcClientExecutor extends AbstractRpcClusterClientExecutor {

	private EtcdClient etcdClient;

	private String namespace = "rpc";

	private String etcdUrl;

	private List<RpcHostAndPort> rpcServersCache = new ArrayList<RpcHostAndPort>();

	private Map<String, List<RpcService>> rpcServiceCache = new ConcurrentHashMap<String, List<RpcService>>();

	private Hashing hashing = new RoundRobinHashing();

	private Logger logger = Logger.getLogger(EtcdRpcClientExecutor.class);

	private EtcdWatchCallback etcdServerWatcher = new EtcdWatchCallback() {
		public void onChange(EtcdChangeResult future) {
			EtcdRpcClientExecutor.this.fetchRpcServers();
		}
	};

	private EtcdWatchCallback etcdServicesWatcher = new EtcdWatchCallback() {
		public void onChange(EtcdChangeResult future) {
			EtcdResult result = future.getResult();
			if (result != null && result.isSuccess()) {
				String key = result.getNode().getKey();
				RpcHostAndPort hostAndPort = EtcdRpcClientExecutor.this
						.getServerAndHost(key);
				if (hostAndPort != null) {
					EtcdRpcClientExecutor.this.fetchRpcServices(hostAndPort);
				}
			}
		}
	};

	private RpcHostAndPort getServerAndHost(String key) {
		for (RpcHostAndPort hostAndPort : rpcServersCache) {
			String serverKey = this.genServerKey(hostAndPort);
			if (key.contains(serverKey)) {
				return hostAndPort;
			}
		}
		return null;
	}

	public EtcdRpcClientExecutor() {

	}

	private String genServerListKey() {
		return "/" + namespace + "/servers";
	}

	private String getServiceListKey(String serverKey) {
		return "/" + namespace + "/services/" + serverKey;
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
		this.etcdClient = new EtcdClient(etcdUrl);
		this.fetchRpcServers();
		this.fetchRpcServices();
	}

	@Override
	public void stopRpcCluster() {
		rpcServersCache = null;
		rpcServiceCache.clear();
	}

	@Override
	public String hash(List<String> servers) {
		return hashing.hash(servers);
	}

	@Override
	public void onClose(RpcHostAndPort hostAndPort) {
		this.closeServer(hostAndPort);
	}

	private void closeServer(RpcHostAndPort hostAndPort) {
		rpcServiceCache.remove(hostAndPort.toString());
		this.removeServer2(hostAndPort.toString());
	}

	private void removeServer2(String server) {
		logger.info("removeServer " + server);
		super.removeServer(server);
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

	private void updateServerNodes(List<EtcdNode> nodes) {
		if (nodes != null) {
			// 获取新的列表和老的列表对比
			HashSet<String> newServers = new HashSet<String>();
			HashSet<String> needAdd = new HashSet<String>();
			HashMap<String, RpcHostAndPort> newServerMap = new HashMap<String, RpcHostAndPort>();
			for (EtcdNode node : nodes) {
				String value = node.getValue();
				RpcHostAndPort hostAndPort = JSONUtils.fromJSON(value,
						RpcHostAndPort.class);
				String key = hostAndPort.toString();
				newServerMap.put(key, hostAndPort);
				newServers.add(key);
				needAdd.add(key);
			}

			// 移除的server节点
			Set<String> oldServers = RpcClusterUtils.toString(rpcServersCache);
			needAdd.removeAll(oldServers);
			oldServers.removeAll(newServers);
			for (String old : oldServers) {
				this.removeServer2(old);
			}

			// 新增加的server节点
			for (String server : needAdd) {
				RpcHostAndPort hostAndPort = newServerMap.get(server);
				this.startConnector(hostAndPort);
				this.fetchRpcServices(hostAndPort);
			}
		}
	}

	private void fetchRpcServers() {
		EtcdResult result = etcdClient.children(this.genServerListKey(), true,
				true);
		if (result.isSuccess()) {
			EtcdNode node = result.getNode();
			List<EtcdNode> nodes = node.getNodes();
			this.updateServerNodes(nodes);
			// 监控节点数据变化
			this.etcdClient.watchChildren(this.genServerListKey(), true, true,
					etcdServerWatcher);
		} else {

		}
	}

	private void fetchRpcServices() {
		for (RpcHostAndPort hostAndPort : rpcServersCache) {
			this.fetchRpcServices(hostAndPort);
		}
	}

	private String genServerKey(RpcHostAndPort hostAndPort) {
		String json = JSONUtils.toJSON(hostAndPort);
		return MD5Utils.md5(json);
	}

	/**
	 * 获取server提供的服务列表
	 * 
	 * @param hostAndPort
	 */
	private void fetchRpcServices(final RpcHostAndPort hostAndPort) {
		String hostAndPortString = hostAndPort.toString();
		String serverKey = this.genServerKey(hostAndPort);
		String serviceListKey = this.getServiceListKey(serverKey);
		EtcdResult result = this.etcdClient
				.children(serviceListKey, true, true);
		if (result.isSuccess()) {
			List<EtcdNode> nodes = result.getNode().getNodes();
			if (nodes != null) {
				ArrayList<RpcService> services = new ArrayList<RpcService>();
				for (EtcdNode node : nodes) {
					String rpcJson = node.getValue();
					RpcService rpcService = JSONUtils.fromJSON(rpcJson,
							RpcService.class);
					services.add(rpcService);
				}
				rpcServiceCache.put(hostAndPortString, services);
			}
			// 监控节点数据变化
			this.etcdClient.watchChildren(serviceListKey, true, true,
					etcdServicesWatcher);
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

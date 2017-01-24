package com.linda.framework.rpc.cluster.limit;

import com.linda.framework.rpc.RemoteCall;
import com.linda.framework.rpc.RpcContext;
import com.linda.framework.rpc.RpcObject;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.filter.RpcFilterChain;
import com.linda.framework.rpc.net.RpcSender;

/**
 * Created by lin on 2017/1/24.
 * 限流
 */
public class LimitFilter implements RpcFilter {

    private LimitCache limitCache;

    public LimitFilter(LimitCache limitCache){
        this.limitCache = limitCache;
    }

    @Override
    public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain) {
        if(limitCache!=null){
            String application = (String)RpcContext.getContext().getAttachment("Application");
            boolean accept = limitCache.accept(application, call.getService(), call.getMethod());
            if(accept){
                chain.nextFilter(rpc, call, sender);
            }else{
                throw new RpcException("request limited ,service is too busy");
            }
        }
    }
}

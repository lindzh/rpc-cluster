package com.linda.framework.rpc.cluster.serializer.simple;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.serializer.RpcSerializer;

import java.io.*;

/**
 * Created by lin on 2016/12/2.
 * 跨语言序列化
 */
public class SimpleSerializer implements RpcSerializer{

    @Override
    public byte[] serialize(Object obj) {
        SimpleOutput out = new SimpleOutput(obj);
        try {
            return out.writeObject();
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        SimpleInput input = new SimpleInput(bytes);
        try {
            return input.readObject();
        } catch (IOException e) {
            throw new RpcException(e);
        } catch (ClassNotFoundException e) {
            throw new RpcException(e);
        } catch (IllegalAccessException e) {
            throw new RpcException(e);
        } catch (InstantiationException e) {
            throw new RpcException(e);
        }
    }
}

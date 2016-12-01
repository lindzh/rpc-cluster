package com.linda.framework.rpc.cluster.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.serializer.RpcSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by lindezhi on 2016/12/1.
 * hessian serialization
 */
public class HessianSerializer implements RpcSerializer {

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hos = new HessianOutput(bos);
        try {
            hos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RpcException(e);
        }finally {
            try {
                bos.close();
            } catch (IOException e) {

            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(bis);
        try {
            return input.readObject();
        } catch (IOException e) {
            throw new RpcException(e);
        }finally {
            try {
                bis.close();
            } catch (IOException e) {

            }
        }
    }
}

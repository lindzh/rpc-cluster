package com.linda.framework.rpc.cluster.serializer.simple;

import com.linda.framework.rpc.serializer.RpcSerializer;

import java.io.*;

/**
 * Created by lin on 2016/12/2.
 */
public class SimpleSerializer implements RpcSerializer{

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        return new byte[0];
    }

    private boolean nativeAndWrite(Object obj,DataOutputStream dos) throws IOException {
        if(obj!=null){
            if(obj.getClass()==int.class){
                dos.writeByte(SimpleConst.intType);
                int v = (Integer)obj;
                dos.writeInt(v);
            }else if(obj.getClass()==long.class){
                dos.writeByte(SimpleConst.longType);
                long v = (Long)obj;
                dos.writeLong(v);
            }else if(obj.getClass()==float.class){
                dos.writeByte(SimpleConst.floatType);
                float v = (Float)obj;
                dos.writeFloat(v);
            }else if(obj.getClass()==double.class){
                dos.writeByte(SimpleConst.doubleType);
                double v = (Double)obj;
                dos.writeDouble(v);
            }else if(obj.getClass()==boolean.class){
                dos.writeByte(SimpleConst.booleanType);
                boolean v = (Boolean)obj;
                dos.writeBoolean(v);
            }else if(obj.getClass()==short.class){
                dos.writeByte(SimpleConst.shortType);
                short v = (Short)obj;
                dos.writeShort(v);
            }else if(obj.getClass()==byte.class){
                dos.writeByte(SimpleConst.byteType);
                byte v = (Byte)obj;
                dos.writeByte(v);
            }else if(obj.getClass()==char.class){
                dos.writeByte(SimpleConst.charType);
                char v = (Character)obj;
                dos.writeChar(v);
            }


            if(obj.getClass()==Integer.class){
                dos.writeByte(SimpleConst.IntegerType);
                int v = (Integer)obj;
                dos.writeInt(v);
            }else if(obj.getClass()==Long.class){
                dos.writeByte(SimpleConst.LongType);
                long v = (Long)obj;
                dos.writeLong(v);
            }else if(obj.getClass()==Float.class){
                dos.writeByte(SimpleConst.FloatType);
                float v = (Float)obj;
                dos.writeFloat(v);
            }else if(obj.getClass()==Double.class){
                dos.writeByte(SimpleConst.DoubleType);
                double v = (Double)obj;
                dos.writeDouble(v);
            }else if(obj.getClass()==Boolean.class){
                dos.writeByte(SimpleConst.BooleanType);
                boolean v = (Boolean)obj;
                dos.writeBoolean(v);
            }else if(obj.getClass()==Short.class){
                dos.writeByte(SimpleConst.ShortType);
                short v = (Short)obj;
                dos.writeShort(v);
            }else if(obj.getClass()==Byte.class){
                dos.writeByte(SimpleConst.ByteType);
                byte v = (Byte)obj;
                dos.writeByte(v);
            }else if(obj.getClass()==Character.class){
                dos.writeByte(SimpleConst.CharacterType);
                char v = (Character)obj;
                dos.writeChar(v);
            }

            else if(obj.getClass()==String.class){
                dos.writeByte(SimpleConst.StringType);
                String str = (String)obj;
                byte[] data = str.getBytes("utf-8");
                dos.writeShort(data.length);
                dos.write(data);
            }
        }
        return false;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);


        return null;
    }
}

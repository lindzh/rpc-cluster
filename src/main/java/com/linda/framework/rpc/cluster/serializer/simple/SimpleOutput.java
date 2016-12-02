package com.linda.framework.rpc.cluster.serializer.simple;

import com.linda.framework.rpc.exception.RpcException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by lin on 2016/12/2.
 * 自定义序列化
 */
public class SimpleOutput {

    private Object obj;

    private DataOutputStream dos = null;

    private ByteArrayOutputStream bos = null;

    public SimpleOutput(Object obj){
        this.obj = obj;
        bos = new ByteArrayOutputStream();
        dos = new DataOutputStream(bos);
    }

    public byte[] writeObject() throws IOException {
        this.writeObject(obj);
        return bos.toByteArray();
    }

    private void writeObject(Object obj) throws IOException {
        if(obj==null){
            dos.writeByte(SimpleConst.ObjectType);
            return;
        }
        Class clazz = obj.getClass();
        if(clazz==int.class){
            this.writeint((Integer)obj);
        }else if(clazz==short.class){
            this.writeshort((Short)obj);
        }else if(clazz==long.class){
            this.writelong((Long)obj);
        }else if(clazz==byte.class){
            this.writebyte((Byte)obj);
        }else if(clazz==float.class){
            this.writefloat((Float)obj);
        }else if(clazz==double.class){
            this.writedouble((Double)obj);
        }else if(clazz==boolean.class){
            this.writeboolean((Boolean)obj);
        }else if(clazz==char.class){
            this.writechar((Character) obj);
        }

        else if(clazz==String.class){
            this.writeString((String)obj);
        }

        else if(clazz==Integer.class){
            this.writeInteger((Integer)obj);
        }else if(clazz==Long.class){
            this.writeLong((Long)obj);
        }else if(clazz==Short.class){
            this.writeShort((Short)obj);
        }else if(clazz==Byte.class){
            this.writeByte((Byte)obj);
        }else if(clazz==Float.class){
            this.writeFloat((Float)obj);
        }else if(clazz==Double.class){
            this.writeDouble((Double)obj);
        }else if(clazz==Boolean.class){
            this.writeBoolean((Boolean)obj);
        }else if(clazz==Character.class){
            this.writeCharacter((Character)obj);
        }

        else if(obj instanceof Set){
            this.writeSet((Set)obj);
        }else if(obj instanceof List){
            this.writeList((List)obj);
        }else if(obj instanceof Map){
            this.writeMap((Map)obj);
        }

        else if(clazz.isArray()){
            this.writeArray((Object[])obj);
        }

        else{
            if(clazz.getName().startsWith("java.")){
                throw new RpcException("not supported object class "+clazz);
            }
            byte dd = SimpleConst.ObjectType|SimpleConst.NotNull;
            dos.writeByte(dd);
            this.writeString(clazz.getName());
            Set<Field> fields = this.getClassField(clazz);
            dos.writeShort((short)fields.size());
            for(Field field:fields){
                this.writeString(field.getName());
                Object value = null;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new IOException(e);
                }
                this.writeObject(value);
            }
        }
    }
    
    private Set<Field> getClassField(Class clazz){
        HashSet<Field> result = new HashSet<Field>();
        if(clazz==Object.class){
            return result;
        }

        Field[] fields = clazz.getDeclaredFields();

        for(Field f:fields){
            int m = f.getModifiers();
            if(Modifier.isFinal(m)){
                continue;
            }
            if(Modifier.isStatic(m)){
                continue;
            }

            f.setAccessible(true);
            result.add(f);
        }

        result.addAll(this.getClassField(clazz.getSuperclass()));
        return result;
    }

    private void writeArray(Object[] arr)throws IOException{
        if(arr!=null){
            dos.writeByte(SimpleConst.ArrayType|SimpleConst.NotNull);
            dos.writeShort((short)arr.length);
            if(arr.length>0){
                for(Object obj:arr){
                    this.writeObject(obj);
                }
            }
        }else{
            dos.writeByte(SimpleConst.ArrayType);
        }
    }

    private void writeSet(Set set)  throws IOException{
        if(set!=null){
            dos.writeByte(SimpleConst.SetType|SimpleConst.NotNull);
            int size = set.size();
            dos.writeShort(size);
            if(size>0){
                for(Object obj:set){
                    this.writeObject(obj);
                }
            }
        }else{
            dos.writeByte(SimpleConst.SetType);
        }
    }

    private void writeList(List list) throws IOException {
        if(list!=null){
            dos.writeByte(SimpleConst.ListType|SimpleConst.NotNull);
            int size = list.size();
            dos.writeShort(size);
            if(size>0){
                for(Object obj:list){
                    this.writeObject(obj);
                }
            }
        }else{
            dos.writeByte(SimpleConst.ListType);
        }
    }

    private void writeMap(Map map) throws IOException {
        if(map!=null){
            dos.writeByte(SimpleConst.MapType|SimpleConst.NotNull);
            int size = map.size();
            dos.writeShort(size);
            if(size>0){
                Set keys = map.keySet();
                for(Object key:keys){
                    System.out.println(key);
                    this.writeObject(key);
                    Object value = map.get(key);
                    this.writeObject(value);
                }
            }
        }else{
            dos.writeByte(SimpleConst.MapType);
        }
    }

    private void writeint(int v) throws IOException {
        dos.writeByte(SimpleConst.intType|SimpleConst.NotNull);
        dos.writeInt(v);
    }

    private void writeshort(short v) throws IOException {
        dos.writeByte(SimpleConst.shortType|SimpleConst.NotNull);
        dos.writeShort(v);
    }

    private void writebyte(byte v) throws IOException {
        dos.writeByte(SimpleConst.byteType|SimpleConst.NotNull);
        dos.writeByte(v);
    }

    private void writelong(long v) throws IOException {
        dos.writeByte(SimpleConst.longType|SimpleConst.NotNull);
        dos.writeLong(v);
    }

    private void writeboolean(boolean v) throws IOException {
        dos.writeByte(SimpleConst.booleanType|SimpleConst.NotNull);
        dos.writeBoolean(v);
    }

    private void writefloat(float v) throws IOException {
        dos.writeByte(SimpleConst.floatType|SimpleConst.NotNull);
        dos.writeFloat(v);
    }

    private void writedouble(double v) throws IOException {
        dos.writeByte(SimpleConst.doubleType|SimpleConst.NotNull);
        dos.writeDouble(v);
    }

    private void writechar(char v) throws IOException {
        dos.writeByte(SimpleConst.charType|SimpleConst.NotNull);
        dos.writeChar(v);
    }

    private void writeInteger(Integer v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.IntegerType|SimpleConst.NotNull);
            dos.writeInt(v);
        }else{
            dos.writeByte(SimpleConst.IntegerType);
        }
    }

    private void writeDouble(Double v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.DoubleType|SimpleConst.NotNull);
            dos.writeDouble(v);
        }else{
            dos.writeByte(SimpleConst.DoubleType);
        }
    }

    private void writeFloat(Float v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.FloatType|SimpleConst.NotNull);
            dos.writeFloat(v);
        }else{
            dos.writeByte(SimpleConst.FloatType);
        }
    }

    private void writeShort(Short v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.ShortType|SimpleConst.NotNull);
            dos.writeShort(v);
        }else{
            dos.writeByte(SimpleConst.ShortType);
        }
    }

    private void writeBoolean(Boolean v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.BooleanType|SimpleConst.NotNull);
            dos.writeBoolean(v);
        }else{
            dos.writeByte(SimpleConst.BooleanType);
        }
    }

    private void writeCharacter(Character v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.CharacterType|SimpleConst.NotNull);
            dos.writeChar(v);
        }else{
            dos.writeByte(SimpleConst.CharacterType);
        }
    }

    private void writeByte(Byte v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.ByteType|SimpleConst.NotNull);
            dos.writeByte(v);
        }else{
            dos.writeByte(SimpleConst.ByteType);
        }
    }

    private void writeLong(Long v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.LongType|SimpleConst.NotNull);
            dos.writeLong(v);
        }else{
            dos.writeByte(SimpleConst.LongType);
        }
    }

    private void writeString(String v) throws IOException {
        if(v!=null){
            dos.writeByte(SimpleConst.StringType|SimpleConst.NotNull);
            byte[] data = v.getBytes("utf-8");
            if(data.length>0){
                dos.writeShort(data.length);
                dos.write(data);
            }else{
                dos.writeShort(0);
            }
        }else{
            dos.writeByte(SimpleConst.StringType);
        }
    }
}

package com.linda.framework.rpc.serialize;

import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.TestBean;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleInput;
import com.linda.framework.rpc.cluster.serializer.simple.SimpleOutput;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lin on 2016/12/2.
 */
public class SimpleoutTest {

    public static void SimpleObjectTest()throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException{
        TestBean testBean = new TestBean();
        testBean.setLimit(4);
        testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
        testBean.setOffset(43432);
        testBean.setOrder("645gdfghdfghdf");

        SimpleOutput simple = new SimpleOutput(testBean);
        byte[] bytes = simple.writeObject();


        String hex = new String(bytes);
        System.out.println(hex);

        SimpleInput sipt = new SimpleInput(bytes);
        Object obj = sipt.readObject();
        System.out.println(JSONUtils.toJSON(obj));
    }

    public static void mapTest() throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        HashMap map = new HashMap();
        map.put(1,555);
        List<Long> list = new ArrayList<Long>();
        list.add(56777L);
        map.put("55666",list);
        map.put("null",null);
        TestBean testBean = new TestBean();
        testBean.setLimit(4);
        testBean.setMessage("ggggggggggggggggggggggggggggggggggggggggggggg");
        testBean.setOffset(43432);
        testBean.setOrder("645gdfghdfghdf");
        map.put("obj",testBean);
        map.put("jhhh",list.toArray());

        SimpleOutput simple = new SimpleOutput(map);
        byte[] bytes = simple.writeObject();

        SimpleInput sipt = new SimpleInput(bytes);
        Object obj = sipt.readObject();
        System.out.println(JSONUtils.toJSON(obj));

    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        mapTest();
    }
}

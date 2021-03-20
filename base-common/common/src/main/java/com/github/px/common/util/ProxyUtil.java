package com.github.px.common.util;

import com.github.px.common.pojo.vo.FileDownloadVO;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class ProxyUtil {
    public static <T>T getInstance(Class<T> tClass, Callback[] callbacks, CallbackFilter callbackFilter){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tClass);
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(callbackFilter);
        return (T) enhancer.create();
    }

    public static void main(String[] args) {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(FileDownloadVO.class);
        beanGenerator.addProperty("username", String.class);
        beanGenerator.addProperty("password", String.class);
        FileDownloadVO obj = (FileDownloadVO) beanGenerator.create();
        Field[] fields = obj.getClass().getDeclaredFields();
        PropertyDescriptor[] propertyDescriptors = CopyUtil.getPropertyDescriptors(obj.getClass());


    }
}

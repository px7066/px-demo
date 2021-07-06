package com.github.px.netty.rpc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediator {
    public static Map<String, MethodBean> methodBeans;

    static {
        methodBeans = new HashMap<>();
    }

    @Setter
    @Getter
    public static class MethodBean{
        private Object bean;

        private Method method;
    }

    public static Response process(RequestFuture requestFuture){
        Response response = new Response();
        try{
            String path = requestFuture.getPath();
            MethodBean methodBean = methodBeans.get(path);
            if(methodBean != null){
                Object bean = methodBean.getBean();
                Method method = methodBean.getMethod();
                Object body = requestFuture.getRequest();
                Class[] paramTypes = method.getParameterTypes();
                Class paramType = paramTypes[0];
                Object param = null;
                if(paramType.isAssignableFrom(List.class)){
                    param = JSONArray.parseArray(JSONArray.toJSONString(body), paramTypes);
                }else if(paramType.getName().equals(String.class.getName())){
                    param = body;
                }else {
                    param = JSONObject.parseObject(JSONObject.toJSONString(body), paramType);
                }
                Object result = method.invoke(bean, param);
                response.setResult(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        response.setId(requestFuture.getId());
        return response;

    }
}

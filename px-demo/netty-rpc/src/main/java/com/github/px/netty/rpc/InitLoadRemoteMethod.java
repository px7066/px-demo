package com.github.px.netty.rpc;

import com.github.px.netty.rpc.annotation.Remote;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class InitLoadRemoteMethod implements ApplicationListener<ContextRefreshedEvent>, Ordered {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> controllerBeans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(Controller.class);
        for (String key : controllerBeans.keySet()) {
            Object bean = controllerBeans.get(key);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Remote remote = method.getAnnotation(Remote.class);
                String methodVal = remote.value();
                Mediator.MethodBean methodBean = new Mediator.MethodBean();
                methodBean.setBean(bean);
                methodBean.setMethod(method);
                Mediator.methodBeans.put(methodVal, methodBean);
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

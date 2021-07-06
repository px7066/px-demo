package com.github.px.netty.rpc;

import com.github.px.netty.rpc.server.NettyServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        new Thread(){
            @Override
            public void run() {
                NettyServer nettyServer = new NettyServer();
                nettyServer.init();
            }
        }.start();
    }
}

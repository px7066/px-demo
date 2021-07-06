package com.github.px.netty.rpc.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
    public static CuratorFramework client;

    public static CuratorFramework create(){
        if(client == null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient("47.105.137.27:2181", 1000, 5000, retryPolicy);
            client.start();
        }
        return client;
    }

    public static CuratorFramework recreate(){
        client = null;
        create();
        return create();
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework client = create();
        client.create().forPath("/netty");
        client.close();
    }
}

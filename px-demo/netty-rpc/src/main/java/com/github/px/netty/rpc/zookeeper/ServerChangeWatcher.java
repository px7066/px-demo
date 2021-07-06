package com.github.px.netty.rpc.zookeeper;

import com.github.px.netty.rpc.client.NettyClient;
import com.github.px.netty.rpc.server.NettyServer;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;

public class ServerChangeWatcher implements CuratorWatcher {

    public static ServerChangeWatcher serverChangeWatcher = null;

    public static final int SERVER_COUNT = 100;

    public static ServerChangeWatcher getInstance(){
        if(serverChangeWatcher == null){
            serverChangeWatcher = new ServerChangeWatcher();
        }
        return serverChangeWatcher;
    }

    @Override
    public synchronized void process(WatchedEvent watchedEvent) throws Exception {
        if(watchedEvent.getState().equals(Watcher.Event.KeeperState.Disconnected)
            || watchedEvent.getState().equals(Watcher.Event.KeeperState.Expired)){
            CuratorFramework client = ZookeeperFactory.recreate();
            client.getChildren().usingWatcher(this).forPath(NettyServer.SERVER_PATH);
            return;
        }else if (watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)
            && !watchedEvent.equals(Watcher.Event.EventType.NodeChildrenChanged)){
            CuratorFramework client =ZookeeperFactory.create();
            client.getChildren().usingWatcher(this).forPath(NettyServer.SERVER_PATH);
            return;
        }
        System.out.println("=============重新初始化服务器连接process================");
        CuratorFramework client = ZookeeperFactory.create();
        String path = NettyServer.SERVER_PATH;
        client.getChildren().usingWatcher(this).forPath(path);
        List<String> serverPaths = client.getChildren().forPath(path);
        List<String> servers = new ArrayList<>();
        for (String serverPath : serverPaths) {
            String[] str = serverPath.split("#");
            int weight = Integer.valueOf(str[2]);
            if(weight > 0){
                for(int w=0;w<weight*SERVER_COUNT;w++){
                    servers.add(str[0]+ "#"+str[1]);
                }
            }
        }
        ChannelFutureManage.serverList.clear();
        ChannelFutureManage.serverList.addAll(servers);
        List<ChannelFuture> futures= new ArrayList<>();
        for (String realServer : ChannelFutureManage.serverList) {
            String[] str = realServer.split("#");
            try {
                ChannelFuture channelFuture = NettyClient.getBootstrap().connect(str[0], Integer.valueOf(str[1])).sync();
                futures.add(channelFuture);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        synchronized (ChannelFutureManage.position){
            ChannelFutureManage.clear();
            ChannelFutureManage.addAll(futures);
        }
    }

    public static void initChannelFuture() throws Exception{
        CuratorFramework client = ZookeeperFactory.create();
        List<String> servers = client.getChildren().forPath(Constants.SERVER_PATH);
        System.out.println("=============初始化服务器连接=======");
        for (String server : servers) {
            String[] str = server.split("#");
            try{
                int weight = Integer.valueOf(str[2]);
                if(weight >= 0){
                    for(int w=0; w<weight*SERVER_COUNT; w++){
                        ChannelFuture channelFuture = NettyClient.getBootstrap().connect(str[0], Integer.valueOf(str[1])).sync();
                        ChannelFutureManage.add(channelFuture);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        client.getChildren().usingWatcher(getInstance()).forPath(Constants.SERVER_PATH);

    }
}

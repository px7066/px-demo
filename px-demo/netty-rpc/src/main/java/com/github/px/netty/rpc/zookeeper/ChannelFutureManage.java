package com.github.px.netty.rpc.zookeeper;

import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelFutureManage {
    static CopyOnWriteArrayList<String> serverList = new CopyOnWriteArrayList<>();

    static AtomicInteger position = new AtomicInteger();

    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    public static ChannelFuture get() throws Exception {
        ChannelFuture channelFuture = get(position);
        if(channelFuture == null){
            ServerChangeWatcher.initChannelFuture();
        }
        return get(position);
    }

    public static ChannelFuture get(AtomicInteger i){
        int size = channelFutures.size();
        if(size == 0){
            return null;
        }
        ChannelFuture channel = null;
        synchronized (i){
            if(i.get() >= size){
                channel = channelFutures.get(0);
                i.set(0);
            }else {
                channel = channelFutures.get(i.getAndIncrement());
            }
            if(!channel.channel().isActive()){
                channelFutures.remove(channel);
                return get(position);
            }
        }
        return channel;
    }

    public static void removeChannel(ChannelFuture channel){
        channelFutures.remove(channel);
    }

    public static void add(ChannelFuture channel){
        channelFutures.add(channel);
    }

    public static void clear(){
        for (ChannelFuture channelFuture : channelFutures) {
            channelFuture.channel().close();
        }
        channelFutures.clear();
    }


    public static void addAll(List<io.netty.channel.ChannelFuture> futures) {
        channelFutures.addAll(futures);
    }
}

package com.github.px.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class NettyClient {

    public static EventLoopGroup group = null;

    public static Bootstrap bootstrap = null;

    public static ChannelFuture future = null;

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        try{
            Promise<Response> promise = new DefaultPromise<>(group.next());
            final ClientHandler handler = new ClientHandler();
            handler.setPromise(promise);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel
                            .pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4 ,0 ,4));
                    nioSocketChannel
                            .pipeline().addLast(new StringDecoder());
                    nioSocketChannel
                            .pipeline().addLast(handler);
                    nioSocketChannel
                            .pipeline().addLast(new LengthFieldPrepender(4, false));
                    nioSocketChannel
                            .pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                }
            });
            future = bootstrap.connect("127.0.0.1", 8080).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Object sendRequest(Object msg){
        try{
            RequestFuture requestFuture = new RequestFuture();
            requestFuture.setResult(msg);
            String requestStr = JSONObject.toJSONString(requestFuture);
            future.channel().writeAndFlush(requestStr);
            Object result = requestFuture.get();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        for(int i=0; i< 100; i++){
            Object result =client.sendRequest("hello");
            System.out.println(result);
        }
    }
}

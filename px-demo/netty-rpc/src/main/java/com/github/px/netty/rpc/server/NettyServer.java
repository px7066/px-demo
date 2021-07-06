package com.github.px.netty.rpc.server;

import com.github.px.netty.rpc.zookeeper.ZookeeperFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;

public class NettyServer {
    public static final String SERVER_PATH = "/netty";

    public static void main(String[] args) {
        init();
    }

    public static void init(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ServerHandler());
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4, false));
                            socketChannel.pipeline().addLast(new StringEncoder());
                        }
                    });
            int port = 8087;
            ChannelFuture future = serverBootstrap.bind(port).sync();
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress netAddress = InetAddress.getLocalHost();
            int weight = 1;
            Stat stat = client.checkExists().forPath(SERVER_PATH);
            if(stat == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(SERVER_PATH, "0".getBytes());
            }
            client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(SERVER_PATH + "/" + netAddress.getHostAddress() + "#" + port + "#" + weight + "#");
            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

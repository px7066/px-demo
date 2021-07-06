package com.github.px.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if(msg instanceof ByteBuf){
//            System.out.println(((ByteBuf) msg).toString(Charset.defaultCharset()));
//        }
//        ctx.channel().writeAndFlush("msg has received!");
        RequestFuture requestFuture = JSONObject.parseObject(msg.toString(),RequestFuture.class);
        long id = requestFuture.getId();
        System.out.println("请求信息为 ===" + msg.toString());
        Response response = new Response();
        response.setId(id);
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
    }
}

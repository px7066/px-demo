package com.github.px.netty.rpc.server;

import com.alibaba.fastjson.JSONObject;
import com.github.px.netty.rpc.Mediator;
import com.github.px.netty.rpc.RequestFuture;
import com.github.px.netty.rpc.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestFuture requestFuture = JSONObject.parseObject(msg.toString(), RequestFuture.class);
        Response response = Mediator.process(requestFuture);
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }
}

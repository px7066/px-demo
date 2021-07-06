package com.github.px.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Promise<Response> promise;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        RequestFuture.received(response);
//        promise.setSuccess(response);
    }
}

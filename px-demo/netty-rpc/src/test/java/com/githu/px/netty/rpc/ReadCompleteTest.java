package com.githu.px.netty.rpc;

import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import org.junit.Test;

public class ReadCompleteTest {
    @Test
    public void guessTest(){
        AdaptiveRecvByteBufAllocator alloctor = new AdaptiveRecvByteBufAllocator();
        RecvByteBufAllocator.Handle handle = alloctor.newHandle();
        System.out.println("===========开始I/O读事件模拟==========");
        handle.reset(null);
        System.out.println(String.format("第一次模拟读，需要分配的大小： %d", handle.guess()));
        handle.lastBytesRead(256);
        handle.readComplete();
        handle.reset(null);
        System.out.println(String.format("第二次模拟读，需要分配的大小： %d", handle.guess()));
        handle.guess();
        handle.lastBytesRead(256);
        handle.readComplete();
        System.out.println("==============连续2次读取的字节数小于默认分配的字节数===");
        handle.reset(null);
        System.out.println(String.format("第三次模拟读，需要分配的大小：%d", handle.guess()));
        handle.guess();
        handle.lastBytesRead(512);
        handle.readComplete();
        System.out.println("===========读取的字节数表达=======");
        handle.reset(null);
        System.out.println(String.format("第四次模拟读，需要分配的大小 %d", handle.guess()));

    }
}

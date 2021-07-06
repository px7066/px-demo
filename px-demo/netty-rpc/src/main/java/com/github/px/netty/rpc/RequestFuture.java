package com.github.px.netty.rpc;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
public class RequestFuture {
    public static Map<Long, RequestFuture> futures = new ConcurrentHashMap<>();

    private long id;

    private Object request;

    private long timeout = 5000;

    private String path;

    private static final AtomicLong aid = new AtomicLong();

    public RequestFuture() {
        id = aid.incrementAndGet();
        addFuture(this);
    }

    public static void addFuture(RequestFuture future){
        futures.put(future.getId(), future);
    }

    public Object get(){
        synchronized (this){
            while (this.request == null){
                try {
                    this.wait(timeout);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return this.request;
    }

    public static void received(Response resp){
        RequestFuture future = futures.remove(resp.getId());
        if(future != null){
            future.setRequest(resp.getResult());
            synchronized (future){
                future.notify();
            }
        }
    }
}

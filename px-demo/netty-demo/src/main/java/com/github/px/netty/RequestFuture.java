package com.github.px.netty;

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

    private Object result;

    private long timeout = 5000;

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
            while (this.result == null){
                try {
                    this.wait(timeout);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return this.result;
    }

    public static void received(Response resp){
        RequestFuture future = futures.remove(resp.getId());
        if(future != null){
            future.setResult(resp.getResult());
            synchronized (future){
                future.notify();
            }
        }
    }
}

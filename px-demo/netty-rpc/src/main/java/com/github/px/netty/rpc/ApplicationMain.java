package com.github.px.netty.rpc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationMain {
    private static volatile  boolean running = true;

    public static void main(String[] args) {
        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.github.px");
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                   try {
                       context.stop();
                   }catch (Throwable t){

                   }
                   synchronized (ApplicationMain.class){
                       running = false;
                       ApplicationMain.class.notify();
                   }
                }
            });
            context.start();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("服务器已启动");
        synchronized (ApplicationMain.class){
            while (running){
                try{
                    ApplicationMain.class.wait();
                }catch (Throwable e){

                }
            }
        }
    }
}

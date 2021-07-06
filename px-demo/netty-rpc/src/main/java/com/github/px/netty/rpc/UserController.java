package com.github.px.netty.rpc;

import com.github.px.netty.rpc.annotation.Remote;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    @Remote("getUserNameById")
    public Object getUserNameById(String userId){
        System.out.println("客户端请求的用户id为===="+userId);
        return "响应结果===用户张三"+userId;
    }
}

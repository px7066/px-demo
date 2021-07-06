package com.github.px.netty.rpc;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private long id;

    private Object result;

}

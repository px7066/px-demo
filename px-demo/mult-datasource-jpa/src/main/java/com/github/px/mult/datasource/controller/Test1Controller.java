package com.github.px.mult.datasource.controller;

import com.github.px.mult.datasource.repository.one.entity.Test1;
import com.github.px.mult.datasource.service.Test1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class Test1Controller {
    @Autowired
    private Test1Service test1Service;

    @GetMapping("saveTest1")
    public void saveTest1(){
        Test1 test1 = new Test1();
        test1Service.add(test1);
    }
}

package com.github.px.batch.job.controller;

import com.github.px.batch.job.domain.user.User;
import com.github.px.batch.job.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class UserTestController {
    @Autowired
    private UserService userService;
    @GetMapping("testSave")
    public void testSave(){
        User user = new User();
        user.setName("123465");
        user.setMobile("1300000000");
        userService.saveUser(user);
    }
}

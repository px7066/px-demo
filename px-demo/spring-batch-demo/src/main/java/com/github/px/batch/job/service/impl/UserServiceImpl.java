package com.github.px.batch.job.service.impl;

import com.github.px.batch.job.domain.user.User;
import com.github.px.batch.job.domain.user.UserDao;
import com.github.px.batch.job.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;


    @Override
    public void saveUser(User user) {
        userDao.save(user);
    }
}

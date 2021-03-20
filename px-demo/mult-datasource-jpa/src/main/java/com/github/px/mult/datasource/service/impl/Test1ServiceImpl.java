package com.github.px.mult.datasource.service.impl;

import com.github.px.mult.datasource.repository.one.dao.Test1Dao;
import com.github.px.mult.datasource.repository.one.entity.Test1;
import com.github.px.mult.datasource.service.Test1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "oneTransactionManager")
public class Test1ServiceImpl implements Test1Service {
    @Autowired
    private Test1Dao test1Dao;


    @Override
    public void add(Test1 test1) {
        test1Dao.save(test1);
    }
}

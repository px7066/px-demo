package com.github.px.batch.job.listener;

import com.github.px.batch.job.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

@Slf4j
public class CsvToDbWriteListener implements ItemWriteListener<User> {
    @Override
    public void beforeWrite(List<? extends User> list) {
        log.info("开始读取csv数据文件-----");
    }

    @Override
    public void afterWrite(List<? extends User> list) {
        log.info("读取完csv数据文件------");
    }

    @Override
    public void onWriteError(Exception e, List<? extends User> list) {
        log.error(e.getMessage(), e);
    }
}

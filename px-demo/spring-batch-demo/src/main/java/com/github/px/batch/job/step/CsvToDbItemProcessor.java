package com.github.px.batch.job.step;

import com.github.px.batch.job.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

@Slf4j
public class CsvToDbItemProcessor extends ValidatingItemProcessor<User> {

    @Override
    public User process(User item) throws ValidationException {
        log.info("csv存储db:process()方法");
        return super.process(item);
    }
}

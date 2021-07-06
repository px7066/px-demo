package com.github.px.batch.job.config;

import com.github.px.batch.job.domain.user.User;
import com.github.px.batch.job.listener.CsvToDbReaderListener;
import com.github.px.batch.job.listener.CsvToDbWriteListener;
import com.github.px.batch.job.step.CsvToDbValidator;
import com.github.px.batch.job.step.CsvToDbItemProcessor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;


@Configuration
public class CsvToDbStepConfig {

    @Bean
    public ItemReader<User> reader() {
        // 使用FlatFileItemReader去读cvs文件，一行即一条数据
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        // 设置文件处在路径
        reader.setResource(new ClassPathResource("static/user.csv"));
        // entity与csv数据做映射
        reader.setLineMapper(new DefaultLineMapper<>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[]{"name", "mobile"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(User.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public ItemProcessor<User, User> processor(){
        CsvToDbItemProcessor csvToDbItemProcessor = new CsvToDbItemProcessor();
        // 设置校验器
        csvToDbItemProcessor.setValidator(csvToDbValidator());
        return csvToDbItemProcessor;
    }

    @Bean
    public Validator<User> csvToDbValidator(){
        return new CsvToDbValidator<>();
    }


    @Bean
    public ItemWriter<User> writer(EntityManagerFactory factory){
        JpaItemWriter<User> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(factory);
        writer.setUsePersist(true);
        return writer;
    }

    @Bean
    public Step myStep(StepBuilderFactory stepBuilderFactory, ItemReader<User> reader,
                       ItemWriter<User> writer, ItemProcessor<User, User> processor){
        return stepBuilderFactory
                .get("csvToDbStep")
                .<User, User>chunk(10) // Chunk的机制(即每次读取一条数据，再处理一条数据，累积到一定数量后再一次性交给writer进行写入操作)
                .reader(reader).faultTolerant().retryLimit(3).retry(Exception.class).skip(Exception.class).skipLimit(2)
                .listener(new CsvToDbReaderListener())
                .processor(processor)
                .writer(writer).faultTolerant().skip(Exception.class).skipLimit(2)
                .listener(new CsvToDbWriteListener())
                .build();
    }

}

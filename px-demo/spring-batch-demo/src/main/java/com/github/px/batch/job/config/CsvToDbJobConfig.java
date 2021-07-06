package com.github.px.batch.job.config;

import com.github.px.batch.job.listener.CsvToDbWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class CsvToDbJobConfig {
    @Bean
    @Primary
    public JobRepository csvToDbJobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDatabaseType("mysql");
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    @Primary
    public SimpleJobLauncher csvToDbJobLauncher(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(csvToDbJobRepository(dataSource, transactionManager));
        return jobLauncher;
    }

    @Bean
    public Job csvToDbJob(JobBuilderFactory jobBuilderFactory, Step step){
        return jobBuilderFactory.get("testJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .listener(new CsvToDbWriteListener())
                .build();
    }
}

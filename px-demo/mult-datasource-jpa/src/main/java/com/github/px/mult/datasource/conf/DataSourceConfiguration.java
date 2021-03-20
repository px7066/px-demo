package com.github.px.mult.datasource.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * <p>数据源配置</p>
 *
 * @author <a href="mailto:7066450@qq.com">panxi</a>
 * @version 1.0.0
 * @date 2021/2/27
 */
@Configuration
public class DataSourceConfiguration {
    @Bean("oneSource")
    @ConfigurationProperties(prefix = "spring.datasource.one")
    @Primary
    public DataSource dataSourceFirst(){
        return DataSourceBuilder.create().build();
    }

    @Bean("twoSource")
    @ConfigurationProperties(prefix = "spring.datasource.two")
    public DataSource dataSourceSecond(){
        return DataSourceBuilder.create().build();
    }

}

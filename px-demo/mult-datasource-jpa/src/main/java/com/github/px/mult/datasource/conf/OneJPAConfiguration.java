package com.github.px.mult.datasource.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EntityScan(basePackages = {"com.github.px.mult.datasource.repository.one.entity", "com.github.px.domain"})
@EnableJpaRepositories(
        basePackages = {"com.github.px.mult.datasource.repository.one"},
        entityManagerFactoryRef = "oneEntityManagerFactoryBean",
        transactionManagerRef = "oneTransactionManager"
)
@EnableTransactionManagement
public class OneJPAConfiguration {
    @Autowired
    @Qualifier("oneSource")
    private DataSource dataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Autowired
    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Bean("oneEntityManagerFactoryBean")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        return entityManagerFactoryBuilder.dataSource(dataSource)
                .properties(properties)
                .persistenceUnit("firstPersistenceUnit")
                .packages("com.github.px.mult.datasource.repository.one")
                .build();
    }

    @Bean("oneTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = entityManagerFactoryBean();
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }

    @Bean(name = "oneEntityManager")
    @Primary
    public EntityManager entityManager() {
        return entityManagerFactoryBean().getObject().createEntityManager();
    }

}

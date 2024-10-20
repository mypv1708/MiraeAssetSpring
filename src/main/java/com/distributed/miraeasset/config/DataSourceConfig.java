package com.distributed.miraeasset.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.distributed.miraeasset.repository",
        entityManagerFactoryRef = "routingEntityManagerFactory",
        transactionManagerRef = "routingTransactionManager"
)
public class DataSourceConfig {
    @Primary
    @Bean(name = "centralDataSource")
    @ConfigurationProperties("spring.datasource.central")
    public DataSource centralDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "branch1DataSource")
    @ConfigurationProperties("spring.datasource.branch1")
    public DataSource branch1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "branch2DataSource")
    @ConfigurationProperties("spring.datasource.branch2")
    public DataSource branch2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "branch3DataSource")
    @ConfigurationProperties("spring.datasource.branch3")
    public DataSource branch3DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "branch4DataSource")
    @ConfigurationProperties("spring.datasource.branch4")
    public DataSource branch4DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public DataSource routingDataSource(
            @Qualifier("centralDataSource") DataSource centralDataSource,
            @Qualifier("branch1DataSource") DataSource branch1DataSource,
            @Qualifier("branch2DataSource") DataSource branch2DataSource,
            @Qualifier("branch3DataSource") DataSource branch3DataSource,
            @Qualifier("branch4DataSource") DataSource branch4DataSource) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("central", centralDataSource);
        targetDataSources.put("branch1", branch1DataSource);
        targetDataSources.put("branch2", branch2DataSource);
        targetDataSources.put("branch3", branch3DataSource);
        targetDataSources.put("branch4", branch4DataSource);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(centralDataSource);
        return routingDataSource;
    }

    @Primary
    @Bean(name = "routingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean routingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        return builder
                .dataSource(routingDataSource)
                .packages("com.distributed.miraeasset.entity")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "routingTransactionManager")
    public PlatformTransactionManager routingTransactionManager(
            @Qualifier("routingEntityManagerFactory") EntityManagerFactory routingEntityManagerFactory) {
        return new JpaTransactionManager(routingEntityManagerFactory);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setDatabase(Database.SQL_SERVER);
        adapter.setDatabasePlatform("org.hibernate.dialect.SQLServerDialect");
        adapter.setGenerateDdl(false);
        return adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter) {
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, new HashMap<>(), null);
    }
}

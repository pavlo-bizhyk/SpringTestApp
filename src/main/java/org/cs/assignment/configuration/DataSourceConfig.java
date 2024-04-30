package org.cs.assignment.configuration;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(System.getenv("POSTGRESQL_URL"));
        dataSourceBuilder.username(System.getenv("POSTGRESQL_NAME"));
        dataSourceBuilder.password(System.getenv("POSTGRESQL_PASSWORD"));
        return dataSourceBuilder.build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(getDataSource());
        entityManagerFactory.setPackagesToScan("org.cs.assignment");
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
        entityManagerFactory.setJpaProperties(properties);

        return entityManagerFactory;
    }
}
package org.skratch.transferservice.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.RequiredArgsConstructor;
import org.skratch.transferservice.constants.DatabasePropertyKeys;
import org.skratch.transferservice.constants.PersistenceConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"org.skratch.transferservice.repository"}, entityManagerFactoryRef = "writerEntityManagerFactory")
@RequiredArgsConstructor
public class WriterPersistenceConfig {

    private final Properties config;

    @Bean(name = "writerUrl")
    public String getWriterDatabaseUrl() {
        return "jdbc:sqlserver://" +
               config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_HOST) +
               ":" +
               config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_PORT) +
               ";databaseName=" +
               config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_DATABASE) +
               ";encrypt=" +
               config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_ENCRYPT, "true") +
               ";trustServerCertificate=" +
               config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_TRUST_SERVER_CERTIFICATE, "true");
    }

    @Bean
    @Primary
    public DataSource writerDataSource(String writerUrl) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(writerUrl);
        dataSource.setDriverClass(config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_DRIVER_CLASS_NAME));
        dataSource.setUser(config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_USERNAME));
        dataSource.setPassword(config.getProperty(DatabasePropertyKeys.SPRING_DATASOURCE_PASSWORD));
        dataSource.setProperties(additionalProperties(writerUrl));

        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean writerEntityManagerFactory(String writerUrl) throws PropertyVetoException {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(writerDataSource(writerUrl));
        factory.setPackagesToScan("org.skratch.ledgerservice.model");
        factory.setJpaProperties(additionalProperties(writerUrl));
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPersistenceUnitName("writer-db");
        return factory;
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(String writerUrl) throws PropertyVetoException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(writerEntityManagerFactory(writerUrl).getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties(String writerUrl) {
        return DatabasePropertyFactory.build(
                DatabasePropertyFactory.Role.WRITER,
                writerUrl,
                config,
                PersistenceConstants.DATABASE_DIALECT,
                PersistenceConstants.DATABASE_NAMING,
                PersistenceConstants.DATABASE_PROVIDER
        );
    }
}

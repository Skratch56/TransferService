package org.skratch.transferservice.config;

import org.skratch.transferservice.constants.DatabasePropertyKeys;

import java.util.Properties;

public class DatabasePropertyFactory {

    public enum Role {READER, WRITER}

    public static Properties build(Role role,
                                   String url,
                                   Properties config,
                                   String dialect,
                                   String namingStrategy,
                                   String providerClass) {

        Properties properties = new Properties();

        properties.setProperty(DatabasePropertyKeys.ENABLE_LAZY_LOAD_NO_TRANS, "true");
        properties.setProperty(DatabasePropertyKeys.DIALECT, dialect);
        properties.setProperty(DatabasePropertyKeys.GENERATE_STATISTICS, "false");
        properties.setProperty(DatabasePropertyKeys.HBM2DDL_AUTO, "validate");
        properties.setProperty(DatabasePropertyKeys.EJB_NAMING_STRATEGY, namingStrategy);
        properties.setProperty(DatabasePropertyKeys.CONNECTION_CHARSET, "UTF-8");
        properties.setProperty(DatabasePropertyKeys.SHOW_SQL, "false");
        properties.setProperty(DatabasePropertyKeys.FORMAT_SQL, "false");
        properties.setProperty(DatabasePropertyKeys.USE_SQL_COMMENTS, "false");
        properties.setProperty(DatabasePropertyKeys.USE_JDBC_METADATA_DEFAULTS, "false");
        properties.setProperty(DatabasePropertyKeys.CONNECTION_PROVIDER_CLASS, providerClass);

        final boolean isReader = (role == Role.READER);

        properties.setProperty(
                DatabasePropertyKeys.CONNECTION_DRIVER_CLASS,
                config.getProperty(isReader ? DatabasePropertyKeys.DB_READER_DRIVER : DatabasePropertyKeys.DB_WRITER_DRIVER)
        );

        properties.setProperty(
                DatabasePropertyKeys.CONNECTION_PASSWORD,
                config.getProperty(isReader ? DatabasePropertyKeys.DB_READER_PASSWORD : DatabasePropertyKeys.DB_WRITER_PASSWORD)
        );

        properties.setProperty(DatabasePropertyKeys.CONNECTION_URL, url);

        properties.setProperty(
                DatabasePropertyKeys.CONNECTION_USERNAME,
                config.getProperty(isReader ? DatabasePropertyKeys.DB_READER_USERNAME : DatabasePropertyKeys.DB_WRITER_USERNAME)
        );

        properties.setProperty(
                DatabasePropertyKeys.C3P0_MAX_SIZE,
                config.getProperty(
                        isReader ? DatabasePropertyKeys.DB_READER_CONNECTION_POOL_MAX_SIZE
                                : DatabasePropertyKeys.DB_WRITER_CONNECTION_POOL_MAX_SIZE,
                        "10"
                )
        );
        properties.setProperty(
                DatabasePropertyKeys.C3P0_MIN_SIZE,
                config.getProperty(
                        isReader ? DatabasePropertyKeys.DB_READER_CONNECTION_POOL_MIN_SIZE
                                : DatabasePropertyKeys.DB_WRITER_CONNECTION_POOL_MIN_SIZE,
                        "5"
                )
        );
        properties.setProperty(
                DatabasePropertyKeys.C3P0_ACQUIRE_INCREMENT,
                config.getProperty(
                        isReader ? DatabasePropertyKeys.DB_READER_ACQUIRE_INCREMENT
                                : DatabasePropertyKeys.DB_WRITER_ACQUIRE_INCREMENT,
                        "5"
                )
        );
        properties.setProperty(
                DatabasePropertyKeys.C3P0_IDLE_TEST_PERIOD,
                config.getProperty(
                        isReader ? DatabasePropertyKeys.DB_READER_CONNECTION_POOL_IDLE_TEST_PERIOD
                                : DatabasePropertyKeys.DB_WRITER_CONNECTION_POOL_IDLE_TEST_PERIOD,
                        "300"
                )
        );

        properties.setProperty(
                DatabasePropertyKeys.C3P0_MAX_STATEMENTS,
                config.getProperty(DatabasePropertyKeys.PAYGATE_READER_CONNECTION_POOL_MAX_STATEMENTS, "0")
        );
        properties.setProperty(
                DatabasePropertyKeys.C3P0_TIMEOUT,
                config.getProperty(DatabasePropertyKeys.PAYGATE_WRITER_CONNECTION_POOL_TIMEOUT, "1")
        );

        return properties;
    }

}

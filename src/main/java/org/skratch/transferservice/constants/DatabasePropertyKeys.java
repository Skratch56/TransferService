package org.skratch.transferservice.constants;

public class DatabasePropertyKeys {

    public static final String DB_READER_ACQUIRE_INCREMENT = "database.PayGateGT.reader.acquireIncrement";
    public static final String DB_READER_ACQUIRE_RETRY_ATTEMPTS = "database.PayGateGT.reader.acquireRetryAttempts";
    public static final String DB_READER_ACQUIRE_RETRY_DELAY = "database.PayGateGT.reader.acquireRetryDelay";
    public static final String DB_READER_CONNECTION_POOL_IDLE_TEST_PERIOD = "database.PayGateGT.reader.connectionPool.idle_test_period";
    public static final String DB_READER_CONNECTION_POOL_MAX_IDLE_TIME = "database.PayGateGT.reader.connectionPool.maxIdleTime";
    public static final String DB_READER_CONNECTION_POOL_MAX_SIZE = "database.PayGateGT.reader.connectionPool.max_size";
    public static final String DB_READER_CONNECTION_POOL_MIN_SIZE = "database.PayGateGT.reader.connectionPool.min_size";
    public static final String DB_READER_DATABASE = "database.PayGateGT.reader.database";
    public static final String DB_READER_DRIVER = "database.PayGateGT.reader.driver";
    public static final String DB_READER_INITIAL_POOL_SIZE = "database.PayGateGT.reader.initialPoolSize";
    public static final String DB_READER_NUM_HELPER_THREADS = "database.PayGateGT.reader.numHelperThreads";
    public static final String DB_READER_PASSWORD = "database.PayGateGT.reader.password";
    public static final String DB_READER_PORT = "database.PayGateGT.reader.port";
    public static final String DB_READER_TEST_CONNECTION_CHECKIN = "database.PayGateGT.reader.testConnectionCheckin";
    public static final String DB_READER_TEST_CONNECTION_CHECKOUT = "database.PayGateGT.reader.testConnectionCheckout";
    public static final String DB_READER_URL = "database.PayGateGT.reader.url";
    // PersistenceConstants Reader Properties
    public static final String DB_READER_USERNAME = "database.PayGateGT.reader.username";
    public static final String DB_WRITER_ACQUIRE_INCREMENT = "database.PayGateGT.writer.acquireIncrement";
    public static final String DB_WRITER_ACQUIRE_RETRY_ATTEMPTS = "database.PayGateGT.writer.acquireRetryAttempts";
    public static final String DB_WRITER_ACQUIRE_RETRY_DELAY = "database.PayGateGT.writer.acquireRetryDelay";
    public static final String DB_WRITER_CONNECTION_POOL_IDLE_TEST_PERIOD = "database.PayGateGT.writer.connectionPool.idle_test_period";
    public static final String DB_WRITER_CONNECTION_POOL_MAX_IDLE_TIME = "database.PayGateGT.writer.connectionPool.maxIdleTime";
    public static final String DB_WRITER_CONNECTION_POOL_MAX_SIZE = "database.PayGateGT.writer.connectionPool.max_size";
    public static final String DB_WRITER_CONNECTION_POOL_MIN_SIZE = "database.PayGateGT.writer.connectionPool.min_size";
    public static final String DB_WRITER_DATABASE = "database.PayGateGT.writer.database";
    public static final String DB_WRITER_DRIVER = "database.PayGateGT.writer.driver";
    public static final String DB_WRITER_INITIAL_POOL_SIZE = "database.PayGateGT.writer.initialPoolSize";
    public static final String DB_WRITER_NUM_HELPER_THREADS = "database.PayGateGT.writer.numHelperThreads";
    public static final String DB_WRITER_PASSWORD = "database.PayGateGT.writer.password";
    public static final String DB_WRITER_PORT = "database.PayGateGT.writer.port";
    public static final String DB_WRITER_TEST_CONNECTION_CHECKIN = "database.PayGateGT.writer.testConnectionCheckin";
    public static final String DB_WRITER_TEST_CONNECTION_CHECKOUT = "database.PayGateGT.writer.testConnectionCheckout";
    public static final String DB_WRITER_URL = "database.PayGateGT.writer.url";
    // PersistenceConstants Writer Properties
    public static final String DB_WRITER_USERNAME = "database.PayGateGT.writer.username";
    public static final String ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
    public static final String CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
    public static final String DIALECT = "hibernate.dialect";
    public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
    public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String EJB_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    public static final String CONNECTION_CHARSET = "hibernate.connection.charSet";
    public static final String PAYGATE_READER_CONNECTION_POOL_MAX_STATEMENTS = "paygate.reader.connectionPool.max_statements";
    public static final String PAYGATE_WRITER_CONNECTION_POOL_TIMEOUT = "paygate.writer.connectionPool.timeout";
    public static final String SHOW_SQL = "hibernate.show_sql";
    public static final String FORMAT_SQL = "hibernate.format_sql";
    public static final String USE_SQL_COMMENTS = "hibernate.use_sql_comments";
    public static final String USE_JDBC_METADATA_DEFAULTS = "hibernate.temp.use_jdbc_metadata_defaults";

    public static final String CONNECTION_PASSWORD = "hibernate.connection.password";
    public static final String CONNECTION_URL = "hibernate.connection.url";
    public static final String CONNECTION_USERNAME = "hibernate.connection.username";

    public static final String CONNECTION_PROVIDER_CLASS = "hibernate.connection.provider_class";

    public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
    public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
    public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
    public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
    public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
    public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";

    public static final String SPRING_DATASOURCE_HOST = "spring.datasource.host";
    public static final String SPRING_DATASOURCE_PORT = "spring.datasource.port";
    public static final String SPRING_DATASOURCE_DATABASE = "spring.datasource.database";
    public static final String SPRING_DATASOURCE_ENCRYPT = "spring.datasource.encrypt";
    public static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "spring.datasource.driverClassName";
    public static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    public static final String SPRING_DATASOURCE_TRUST_SERVER_CERTIFICATE = "spring.datasource.trustServerCertificate";

    private DatabasePropertyKeys() {
    }
}

package org.skratch.transferservice.constants;

public class PersistenceConstants {

    public static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DATABASE_DIALECT = "org.hibernate.dialect.MySQLDialect";
    public static final String DATABASE_NAMING = "org.hibernate.cfg.ImprovedNamingStrategy";
    public static final String DATABASE_PROVIDER = "org.hibernate.connection.C3P0ConnectionProvider";

    private PersistenceConstants() {
    }
}
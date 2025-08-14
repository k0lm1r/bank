package transactions.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class Database {
    private static HikariDataSource dataSource;
    private static HikariConfig config = new HikariConfig();

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/bank_db");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMaximumPoolSize(8);
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void processException(SQLException e) {
        System.out.println(e.getMessage() + '\n' + e.getSQLState());
    }

    public abstract boolean createTable();
}
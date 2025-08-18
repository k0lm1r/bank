package transactions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
    private static HikariDataSource dataSource;
    private static HikariConfig config = new HikariConfig();
    public static Timestamp startTime = new Timestamp(System.currentTimeMillis());

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
        System.out.println(e.getMessage() + ' ' + e.getSQLState());
    }

    public static void close() {
        dataSource.close();
    }

    public static boolean createTable(String tableName, String creatingPattern) {
        boolean isCreated = false;

        try (Connection con = getConnection(); Statement state = con.createStatement()) {
            if (con.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"}).next()) {
                isCreated = false;
            } else {
                state.execute(creatingPattern);
                isCreated = true;
            }
        } catch (SQLException e) {
            processException(e);
        }

        return isCreated;
    }
}
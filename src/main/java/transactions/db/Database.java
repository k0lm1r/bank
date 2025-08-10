package transactions.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
    private static String url = "jdbc:postgresql://localhost:5432/bank_db";
    private static String user = "postgres";
    private static String password = "postgres";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password); 
    }

    public abstract boolean createTable();
}
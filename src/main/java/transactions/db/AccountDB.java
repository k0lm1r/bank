package transactions.db;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountDB extends Database {
    @Override
    public boolean createTable() {
        try (Connection con = Database.getConnection()) {
            if (con.getMetaData().getTables(null, null, "accounts", new String[] {"TABLE"}).next()) 
                return false;
                
            
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\n" + e.getSQLState());
        }

        return true;
    }
}

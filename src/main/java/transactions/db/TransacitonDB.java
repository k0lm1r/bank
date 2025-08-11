package transactions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TransacitonDB extends Database {
    private static final String tableName = "transaction";

    @Override
    public boolean createTable() {
        boolean isCreated = false;

        try (Connection con = Database.getConnection(); Statement state = con.createStatement()) {
            if (con.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"}).next()) {
                isCreated = true;
            } else {
                isCreated = state.execute("CREATE TABLE " + tableName + " (" +
                "transaction_id INT PRIMARY GENERATED ALWAYS AS IDENTITY," + 
                "sender_id INT," +
                "recipient_id INT" +
                "sum DECIMAL(6, 2)," +
                "FOREIGN KEY (sender_id) REFERENCES account (account_id)," + 
                "FOREIGN KEY (recipient_id) REFERENCES account (account_id)" +
                ");");
            }
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isCreated;
    }

    
}

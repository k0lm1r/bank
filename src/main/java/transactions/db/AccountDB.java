package transactions.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import transactions.models.Account;

public class AccountDB extends Database {
    private final String tableName = "account";

    @Override
    public boolean createTable() {
        boolean isCreated = false;

        try (Connection con = Database.getConnection(); Statement state = con.createStatement()) {
            if (con.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"}).next()) {
                isCreated = true;
            } else {
                isCreated = state.execute("CREATE TABLE " + tableName + " (" +
                "account_id INT PRIMARY GENERATED ALWAYS AS IDENTITY," + 
                "name VARCHAR(50)," +
                "balance DECIMAL(6, 2)," +
                ");");
            }
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isCreated;
    }

    public boolean insertAccount(Account newAccount) {
        String sql = "INSERT ?(name, balance) VALUES(?, ?);";
        boolean isInserted = false;

        try (Connection con = Database.getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setString(1, tableName);
            state.setString(2, newAccount.getOwnerName());
            state.setBigDecimal(3, newAccount.getBalance());
            isInserted = state.executeUpdate() == 1;
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isInserted;
    }
}

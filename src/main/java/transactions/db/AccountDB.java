package transactions.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import transactions.models.Account;

public class AccountDB extends Database {
    @Override
    public boolean createTable() {
        boolean isCreated = false;

        try (Connection con = Database.getConnection(); Statement state = con.createStatement()) {
            if (con.getMetaData().getTables(null, null, "account", new String[] {"TABLE"}).next()) {
                isCreated = true;
            } else {
                isCreated = state.execute("CREATE TABLE account (" +
                "account_id INT PRIMARY GENERATED ALWAYS AS IDENTITY," + 
                "balance DECIMAL(6, 2)," +
                ");");
            }
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isCreated;
    }

    public boolean insertAccount(Account newAccount) {
        String sql = "INSERT account(balance) VALUES(?);";
        boolean isInserted = false;

        try (Connection con = Database.getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setBigDecimal(1, newAccount.getBalance());
            isInserted = state.executeUpdate() == 1;
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isInserted;
    }

    public static BigDecimal takeAccountBalance(long id) {
        String sql = "SELECT balance FROM account WHERE id = ?;";
        BigDecimal balance = null;

        try (Connection con = Database.getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setLong(1, id);
            ResultSet res = state.executeQuery();
            balance = res.getBigDecimal("balance");
            res.close();
        } catch (SQLException e) {
            Database.processException(e);
        }

        return balance;
    }
}

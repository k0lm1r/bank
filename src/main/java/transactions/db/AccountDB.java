package transactions.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import transactions.models.Account;

public class AccountDB {
    public static boolean createTable() {
        String creatingPattern = "CREATE TABLE account (" +
                "account_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," + 
                "balance DECIMAL(7, 2)" +
                ");";
        return Database.createTable("account", creatingPattern);
    }

    public static boolean insertAccount(Account newAccount) {
        String sql = "INSERT INTO account (balance) VALUES(?);";
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
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        BigDecimal balance = null;

        try (Connection con = Database.getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setLong(1, id);

            ResultSet res = state.executeQuery();
            while (res.next())
                balance = res.getBigDecimal("balance");
            res.close();
        } catch (SQLException e) {
            Database.processException(e);
        }

        return balance;
    }

    public static long getCount() {
        long count = 0;

        try (Connection con = Database.getConnection(); Statement state = con.createStatement(); 
                ResultSet countSet = state.executeQuery("SELECT COUNT(*) FROM account;");) {
            while (countSet.next()) count = countSet.getLong(1);
        } catch (SQLException e) {
            Database.processException(e);
        }

        return count;
    }

    public static void updateBalance(long id, BigDecimal delta) {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";
        if (delta != null) {
            try (Connection con = Database.getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
                state.setBigDecimal(1, delta);
                state.setLong(2, id);
                state.executeUpdate();
            } catch (SQLException e) {
                Database.processException(e);
            }
        }
    }
}

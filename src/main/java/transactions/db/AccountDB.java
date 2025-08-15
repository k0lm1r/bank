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

        try (Connection con = getConnection(); Statement state = con.createStatement()) {
            if (con.getMetaData().getTables(null, null, "account", new String[] {"TABLE"}).next()) {
                isCreated = false;
            } else {
                isCreated = state.execute("CREATE TABLE account (" +
                "account_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," + 
                "balance DECIMAL(7, 2)" +
                ");");
            }
        } catch (SQLException e) {
            processException(e);
        }

        return isCreated;
    }

    public static boolean insertAccount(Account newAccount) {
        String sql = "INSERT INTO account (balance) VALUES(?);";
        boolean isInserted = false;

        try (Connection con = getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setBigDecimal(1, newAccount.getBalance());
            isInserted = state.executeUpdate() == 1;
        } catch (SQLException e) {
            processException(e);
        }

        return isInserted;
    }

    public static BigDecimal takeAccountBalance(long id) {
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        BigDecimal balance = null;

        try (Connection con = getConnection(); PreparedStatement state = con.prepareStatement(sql)) {
            state.setLong(1, id);

            ResultSet res = state.executeQuery();
            while (res.next())
                balance = res.getBigDecimal("balance");
            res.close();
        } catch (SQLException e) {
            processException(e);
        }

        return balance;
    }
}

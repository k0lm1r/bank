package transactions.db;

import java.math.BigDecimal;
import java.sql.*;
import transactions.models.Transaction;

public class TransactionDB extends Database {
    @Override
    public boolean createTable() {
        boolean isCreated = false;

        try (Connection con = getConnection(); ) {
            Statement state = con.createStatement();

            if (con.getMetaData().getTables(null, null, "transaction", new String[] {"TABLE"}).next()) {
                isCreated = true;
            } else {
                isCreated = state.execute("CREATE TABLE transaction (" +
                "transaction_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," + 
                "sender_id INT," +
                "recipient_id INT," +
                "sum DECIMAL(7, 2)," +
                "FOREIGN KEY (sender_id) REFERENCES account (account_id)," + 
                "FOREIGN KEY (recipient_id) REFERENCES account (account_id)" +
                ");");
            }

            state.close();
        } catch (SQLException e) {
            processException(e);
        }

        return isCreated;
    }

    private static boolean validateTransaction(Transaction newTransaction) {
        BigDecimal senderBalance = AccountDB.takeAccountBalance(newTransaction.getSendersId());
        BigDecimal delta = takeDelta(newTransaction.getSendersId());

        if (newTransaction.getRecipientId() == newTransaction.getSendersId()) {
            return false;
        } else if (senderBalance == null || AccountDB.takeAccountBalance(newTransaction.getRecipientId()) == null) {
            return false;
        } else if (delta == null && senderBalance.compareTo(newTransaction.getTransactionSum()) == -1) {
            return false;
        } else if (delta != null && senderBalance.add(delta).compareTo(newTransaction.getTransactionSum()) == -1) {
            return false;
        }

        return true;
    }

    private static BigDecimal takeDelta(long id) {
        BigDecimal delta = null;
        String sqlIncome = "SELECT SUM(sum) FROM transaction WHERE recipient_id = ?;";
        String sqlExpence = "SELECT SUM(sum) FROM transaction WHERE sender_id = ?;";

        try (Connection con = getConnection()) {
            PreparedStatement stateIncome = con.prepareStatement(sqlIncome), stateExpence = con.prepareStatement(sqlExpence);

            stateIncome.setLong(1, id);
            stateExpence.setLong(1, id);

            ResultSet resIncome = stateIncome.executeQuery(), resExpence = stateExpence.executeQuery();
            while (resIncome.next() && resExpence.next()) {
                BigDecimal income = resIncome.getBigDecimal("sum"), expence = resExpence.getBigDecimal("sum");
                if (income != null && expence != null) 
                    delta = income.add(expence.negate());
            }

            resIncome.close(); resExpence.close(); stateIncome.close(); stateExpence.close();
        } catch (SQLException e) {
            processException(e);
        }

        return delta;
    }

    public static boolean insertTransaction(Transaction newTransaction) {
        boolean isInserted = false;

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            String blockSql = "SELECT * FROM account WHERE account_id = ? FOR UPDATE;";

            try (PreparedStatement block = con.prepareStatement(blockSql)) {
                block.setQueryTimeout(2);
                block.setLong(1, newTransaction.getSendersId());
                block.execute();
            }

            if (validateTransaction(newTransaction)) {
                con.commit();
                String insertSql = "INSERT INTO transaction(sender_id, recipient_id, sum) VALUES (?, ?, ?);";
                try (PreparedStatement insert = con.prepareStatement(insertSql)) {
                    insert.setLong(1, newTransaction.getSendersId());
                    insert.setLong(2, newTransaction.getRecipientId());
                    insert.setBigDecimal(3, newTransaction.getTransactionSum());
                    insert.executeUpdate();
                    con.commit();
                    isInserted = true;
                }
            } else 
                con.rollback();
        } catch (SQLException e) {
            processException(e);
        }

        return isInserted;
    }
}

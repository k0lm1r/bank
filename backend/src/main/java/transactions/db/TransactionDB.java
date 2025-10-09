package transactions.db;

import java.math.BigDecimal;
import java.sql.*;
import transactions.models.Transaction;

public class TransactionDB {
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

    public static BigDecimal takeDelta(long id) {
        BigDecimal delta = null;
        String sqlIncome = "SELECT SUM(sum) FROM transaction WHERE recipient_id = ? AND time >= ?;";
        String sqlExpence = "SELECT SUM(sum) FROM transaction WHERE sender_id = ? AND time >= ?;";

        try (Connection con = Database.getConnection()) {
            PreparedStatement stateIncome = con.prepareStatement(sqlIncome), stateExpence = con.prepareStatement(sqlExpence);

            stateIncome.setLong(1, id);
            stateIncome.setTimestamp(2, Database.startTime);
            stateExpence.setLong(1, id);
            stateExpence.setTimestamp(2, Database.startTime);

            ResultSet resIncome = stateIncome.executeQuery(), resExpence = stateExpence.executeQuery();
            while (resIncome.next() && resExpence.next()) {
                BigDecimal income = resIncome.getBigDecimal("sum"), expence = resExpence.getBigDecimal("sum");
                if (income != null && expence != null) {
                    delta = income.add(expence.negate());
                } else if (income == null && expence != null) {
                    delta = expence.negate();
                } else if (income != null)
                    delta = income;
            }

            resIncome.close(); resExpence.close(); stateIncome.close(); stateExpence.close();
        } catch (SQLException e) {
            Database.processException(e);
        }

        return delta;
    }

    public static boolean insertTransaction(Transaction newTransaction) {
        boolean isInserted = false;

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            String blockSql = "SELECT * FROM account WHERE account_id = ? FOR UPDATE;";

            try (PreparedStatement block = con.prepareStatement(blockSql)) {
                block.setQueryTimeout(2);
                block.setLong(1, newTransaction.getSendersId());
                block.execute();
            }

            if (validateTransaction(newTransaction)) {
                con.commit();
                con.setAutoCommit(true);
                String insertSql = "INSERT INTO transaction(sender_id, recipient_id, sum, time) VALUES (?, ?, ?, ?);";
                try (PreparedStatement insert = con.prepareStatement(insertSql)) {
                    insert.setLong(1, newTransaction.getSendersId());
                    insert.setLong(2, newTransaction.getRecipientId());
                    insert.setBigDecimal(3, newTransaction.getTransactionSum());
                    insert.setTimestamp(4, newTransaction.getTransactionTime());
                    insert.executeUpdate();
                    isInserted = true;
                }
            } else {
                con.rollback();
                System.out.println("Reject: " + newTransaction);
            }
        } catch (SQLException e) {
            Database.processException(e);
        }

        return isInserted;
    }
}

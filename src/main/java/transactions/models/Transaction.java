package transactions.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private BigDecimal transactionSum;
    private long sendersId;
    private long recipientId;
    private Timestamp transactionTime;

    public Transaction(BigDecimal transactionSum, long sendersId, long recipientId) {
        this.transactionSum = transactionSum;
        this.sendersId = sendersId;
        this.recipientId = recipientId;
        this.transactionTime = new Timestamp(System.currentTimeMillis());
    }

    public BigDecimal getTransactionSum() {
        return transactionSum;
    }

    public long getSendersId() {
        return sendersId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public Timestamp getTransactionTime() {
        return this.transactionTime;
    }

    @Override
    public String toString() {
        return sendersId + " " + recipientId + " " + transactionSum + " " + transactionTime;
    }
}

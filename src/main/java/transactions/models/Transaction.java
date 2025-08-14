package transactions.models;

import java.math.BigDecimal;

public class Transaction {
    private BigDecimal transactionSum;
    private long sendersId;
    private long recipientId;

    public Transaction(BigDecimal transactionSum, long sendersId, long recipientId) {
        this.transactionSum = transactionSum;
        this.sendersId = sendersId;
        this.recipientId = recipientId;
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
}

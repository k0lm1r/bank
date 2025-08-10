package transactions.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private BigDecimal transactionSum;
    private LocalDate transactionDate;
    private long sendersId;
    private long recipientId;

    public Transaction(BigDecimal transactionSum, LocalDate transactionDate, long sendersId, long recipientId) {
        this.transactionSum = transactionSum;
        this.transactionDate = transactionDate;
        this.sendersId = sendersId;
        this.recipientId = recipientId;
    }

    public BigDecimal getTransactionSum() {
        return transactionSum;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public long getSendersId() {
        return sendersId;
    }

    public long getRecipientId() {
        return recipientId;
    }
}

package transactions.models;

import java.math.BigDecimal;

public class Account {
    private BigDecimal balance;
    private String ownerName;

    public Account(BigDecimal balance, String ownerName) {
        this.balance = balance; this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

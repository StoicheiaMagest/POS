package org.example;

public class Money {
    private final long amount;

    public Money(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }
}
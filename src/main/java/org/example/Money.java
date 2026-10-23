package org.example;

public class Money {
    private final int amount;
    public Money(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
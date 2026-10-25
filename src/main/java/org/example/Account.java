package org.example;

public class Account {

    private long balance;

    public Account() {
        this(0);
    }

    public Account(long amount) {
        this.balance = amount;
    }

    public boolean transfer(long amount, Account recipient) {
        if (recipient == null || !checkCanTransfer(amount)) {
            return false;
        }

        balance -= amount;
        recipient.addToBalance(amount);
        return true;
    }

    public void addToBalance(long amount) {
        balance += amount;
    }

    public void addToBalance(Money money) {
        balance += money.getAmount();
    }

    public boolean checkCanTransfer(long amount) {
        return amount >= 0 && amount <= balance;
    }
}

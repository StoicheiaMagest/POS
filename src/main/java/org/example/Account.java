package org.example;

public class Account {

    private int balance;

    public Account() {
        this(0);
    }

    public Account(int amount) {
        this.balance = amount;
    }

    public boolean transfer(int amount, Account recipient) {
        if (recipient == null || !checkCanTransfer(amount)) {
            return false;
        }

        balance -= amount;
        recipient.addToBalance(amount);
        return true;
    }

    public void addToBalance(int amount) {
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public void addToBalance(Money money) {
        balance += money.getAmount();
    }

    public boolean checkCanTransfer(int amount) {
        return amount >= 0 && amount <= balance;
    }
}

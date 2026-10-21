package org.example;

/**
 * The shop itself: a name plus the bank account that receives card
 * payments (spec &sect;6 — the only new domain class of v1).
 */
public class Store {

    private final String name;
    private final Account bankAccount;

    public Store(String name, Account bankAccount) {
        this.name = name;
        this.bankAccount = bankAccount;
    }

    public String getName() {
        return name;
    }

    public Account getBankAccount() {
        return bankAccount;
    }
}

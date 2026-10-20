package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void defaultConstructorStartsWithZeroBalance() {
        Account account = new Account();

        assertFalse(account.checkCanTransfer(1));
        assertTrue(account.checkCanTransfer(0));
    }

    @Test
    void amountConstructorSetsInitialBalance() {
        Account account = new Account(50);

        assertTrue(account.checkCanTransfer(50));
        assertFalse(account.checkCanTransfer(51));
    }

    @Test
    void addToBalance_increasesBalance() {
        Account account = new Account(20);

        account.addToBalance(15);

        assertTrue(account.checkCanTransfer(35));
        assertFalse(account.checkCanTransfer(36));
    }

    @Test
    void transfer_movesMoneyBetweenAccounts() {
        Account sender = new Account(100);
        Account recipient = new Account(20);

        assertTrue(sender.transfer(40, recipient));

        assertTrue(sender.checkCanTransfer(60));
        assertFalse(sender.checkCanTransfer(61));
        assertTrue(recipient.checkCanTransfer(60));
        assertFalse(recipient.checkCanTransfer(61));
    }

    @Test
    void transfer_failsWhenAmountExceedsBalance() {
        Account sender = new Account(30);
        Account recipient = new Account(20);

        assertFalse(sender.transfer(31, recipient));

        assertTrue(sender.checkCanTransfer(30));
        assertTrue(recipient.checkCanTransfer(20));
    }

    @Test
    void checkCanTransfer_returnsFalseWhenAmountExceedsBalance() {
        Account account = new Account(10);

        assertFalse(account.checkCanTransfer(11));
    }

    @Test
    void transfer_allFunds_succeeds() {
        Account sender = new Account(30);
        Account recipient = new Account(0);

        assertTrue(sender.transfer(30, recipient));
        assertFalse(sender.checkCanTransfer(1));
        assertTrue(recipient.checkCanTransfer(30));
    }


}

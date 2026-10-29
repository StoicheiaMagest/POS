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

    @Test
    void checkCanTransfer_returnsFalseForNegativeAmount() {
        Account account = new Account(10);

        assertFalse(account.checkCanTransfer(-1));
    }

    @Test
    void transfer_failsWhenRecipientIsNull() {
        Account sender = new Account(30);

        assertFalse(sender.transfer(10, null));

        // A failed transfer should leave the sender's balance unchanged.
        assertTrue(sender.checkCanTransfer(30));
        assertFalse(sender.checkCanTransfer(31));
    }

    @Test
    void transfer_failsWhenAmountIsNegative() {
        Account sender = new Account(30);
        Account recipient = new Account(20);

        assertFalse(sender.transfer(-1, recipient));

        // A failed transfer should leave both balances unchanged.
        assertTrue(sender.checkCanTransfer(30));
        assertTrue(recipient.checkCanTransfer(20));
    }

    @Test
    void transfer_zeroAmount_succeeds() {
        Account sender = new Account(30);
        Account recipient = new Account(20);

        assertTrue(sender.transfer(0, recipient));

        // The balances should remain unchanged.
        assertTrue(sender.checkCanTransfer(30));
        assertTrue(recipient.checkCanTransfer(20));
    }


    @Test
    void addToBalance_addsMoneyAmountToExistingBalance() {
        Account account = new Account(20);

        account.addToBalance(new Money(15));

        assertTrue(account.checkCanTransfer(35));
        assertFalse(account.checkCanTransfer(36));
    }

    @Test
    void addToBalance_addsMoneyToZeroBalance() {
        Account account = new Account();

        account.addToBalance(new Money(7));

        assertTrue(account.checkCanTransfer(7));
        assertFalse(account.checkCanTransfer(8));
    }



}

package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the consolidated payment card (spec &sect;6): a card carries an
 * id, an issuer and a linked bank account, and pays by transferring from
 * that account. Replaces the old duplicated Card/PaymentCard tests.
 */
public class PaymentCardTest {

    @Test
    void payTransfersFromLinkedAccountToRecipient() {
        Account customerAccount = new Account(1000);
        Account storeAccount = new Account(0);
        PaymentCard card = new PaymentCard(1, "Visa", customerAccount);

        boolean paid = card.pay(400, storeAccount);

        assertTrue(paid);
        assertEquals(600, customerAccount.getBalance());
        assertEquals(400, storeAccount.getBalance());
    }

    @Test
    void payReturnsFalseAndTransfersNothingWhenBalanceTooLow() {
        Account customerAccount = new Account(100);
        Account storeAccount = new Account(0);
        PaymentCard card = new PaymentCard(2, "MasterCard", customerAccount);

        boolean paid = card.pay(400, storeAccount);

        assertFalse(paid);
        assertEquals(100, customerAccount.getBalance());
        assertEquals(0, storeAccount.getBalance());
    }

    @Test
    void cardExposesIdIssuerAndBalance() {
        PaymentCard card = new PaymentCard(42, "Amex", new Account(50000));

        assertEquals(42, card.getCardId());
        assertEquals("Amex", card.getIssuer());
        assertEquals(50000, card.getBalance());
    }
}

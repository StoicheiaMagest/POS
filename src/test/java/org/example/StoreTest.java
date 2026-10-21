package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the store: a named shop with a bank account that receives card
 * payments (spec &sect;6, only new domain class).
 */
public class StoreTest {

    @Test
    void storeHasNameAndBankAccount() {
        Account account = new Account(0);

        Store store = new Store("Kwik E-Smart", account);

        assertEquals("Kwik E-Smart", store.getName());
        assertEquals(account, store.getBankAccount());
    }
}

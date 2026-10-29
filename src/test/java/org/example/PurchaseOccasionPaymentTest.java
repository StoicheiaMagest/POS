package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class PurchaseOccasionPaymentTest {

    private Store createStoreWithBalance(long balanceInOre) {
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        Account storeBankAccount = new Account(balanceInOre);
        return new Store(registry, storeBankAccount, new DiscountHandler(), scanner);
    }

    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(priceInOre);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        return item;
    }

    @Test
    void payPurchaseTransfersTotalFromCustomerCardToStoreBankAccount() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        Account customerBankAccount = new Account(1000);
        customer.setCard(new Card(5555L, customerBankAccount));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        boolean paid = purchase.payPurchase();

        assertTrue(paid);
        assertEquals(250L, store.getStoreBankAccount().getBalance());
        assertEquals(750L, customerBankAccount.getBalance());
    }

    @Test
    void payPurchaseFailsWhenCardAccountDoesNotCoverTotal() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        Account poorAccount = new Account(100);
        customer.setCard(new Card(5555L, poorAccount));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        boolean paid = purchase.payPurchase();

        assertFalse(paid);
        // nothing moved: the failed transfer leaves both accounts untouched
        assertEquals(0L, store.getStoreBankAccount().getBalance());
        assertEquals(100L, poorAccount.getBalance());
    }

    @Test
    void payPurchaseFailsWhenCustomerHasNoCard() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));

        Customer customerWithoutCard = new Customer("Stig", "19900101-1234", false, false);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customerWithoutCard, store);

        boolean paid = purchase.payPurchase();

        assertFalse(paid);
        assertEquals(0L, store.getStoreBankAccount().getBalance());
    }

    // an occasion that is already paid must never take money a second time
    @Test
    void payingTwiceFails() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        Account customerBankAccount = new Account(1000);
        customer.setCard(new Card(5555L, customerBankAccount));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        assertTrue(purchase.payPurchase());

        assertThrows(IllegalStateException.class, () -> purchase.payPurchase());
        // the refused second attempt moved no money
        assertEquals(250L, store.getStoreBankAccount().getBalance());
        assertEquals(750L, customerBankAccount.getBalance());
    }

    @Test
    void isPaidIsFalseBeforePayPurchaseIsCalled() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));
        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        customer.setCard(new Card(5555L, new Account(1000)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        assertFalse(purchase.isPaid());
    }

    @Test
    void isPaidIsTrueAfterSuccessfulPayment() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));
        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        customer.setCard(new Card(5555L, new Account(1000)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);
        purchase.payPurchase();

        assertTrue(purchase.isPaid());
    }

    @Test
    void isPaidIsFalseWhenCardAccountDoesNotCoverTotal() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));
        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        customer.setCard(new Card(5555L, new Account(100)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);
        purchase.payPurchase();

        assertFalse(purchase.isPaid());
    }

    @Test
    void isPaidIsFalseWhenCustomerHasNoCard() {
        Store store = createStoreWithBalance(0);
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(250));
        Customer customerWithoutCard = new Customer("Stig", "19900101-1234", false, false);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customerWithoutCard, store);
        purchase.payPurchase();

        assertFalse(purchase.isPaid());
    }
}

package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        customer.setCard(new Card(customerBankAccount));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        boolean paid = purchase.payPurchase();

        assertTrue(paid);
        assertEquals(250L, store.getStoreBankAccount().getBalance());
        assertEquals(750L, customerBankAccount.getBalance());
    }
}

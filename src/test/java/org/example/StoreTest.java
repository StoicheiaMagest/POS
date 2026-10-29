package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StoreTest {

    @Test
    public void storeReturnsThePartsItWasBuiltWith(){
        ArticleRegistry registry = new ArticleRegistry();
        Account account = new Account();
        DiscountHandler discountHandler = new DiscountHandler();
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Store store = new Store(registry, account, discountHandler, scanner);

        assertEquals(registry, store.getArticleRegistry());
        assertEquals(account, store.getStoreBankAccount());
        assertEquals(discountHandler, store.getDiscountHandler());
        assertEquals(scanner, store.getBarcodeScanner());
    }
}

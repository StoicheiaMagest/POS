package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PurchaseOccasionPrintReceiptTest {

    private PurchaseOccasion purchaseOf(Basket basket) {
        return new PurchaseOccasion(basket, mock(Customer.class), mock(Account.class));
    }

    @Test
    void printReceiptShowsZeroTotalForEmptyBasket() {
        assertEquals("Kvitto\nAntal varor: 0\nTotalt: 0,00 SEK",
                purchaseOf(new Basket()).printReceipt());
    }

    @Test
    void printReceiptShowsCountAndTotal() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Milk", 1000, Vat.FOOD, 111));
        basket.addItem(new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));

        assertEquals("Kvitto\nAntal varor: 2\nTotalt: 33,60 SEK",
                purchaseOf(basket).printReceipt());
    }

    @Test
    void printReceiptGroupsThousandsWithNonBreakingSpace() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("TV", 100000, Vat.STANDARD, 999));

        assertEquals("Kvitto\nAntal varor: 1\nTotalt: 1 250,00 SEK",
                purchaseOf(basket).printReceipt());
    }
}

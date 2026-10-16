package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {

    @Test
    void testGetReceiptNumber() {
        int expectedReceiptNumber = 1001;
        LocalDateTime receiptDate = LocalDateTime.now();
        Receipt receipt = new Receipt(expectedReceiptNumber, receiptDate, "storeName");

        assertEquals(expectedReceiptNumber, receipt.getReceiptNumber());
    }

    @Test
    void testGetDateTime() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 6, 1, 12, 30);
        Receipt receipt = new Receipt(1001, expectedDateTime, "storeName");

        assertEquals(expectedDateTime, receipt.getReceiptDateAndTime());
    }

    @Test
    void testGetStoreName() {
        String expectedStoreName = "Java Craftsman Store";
        LocalDateTime receiptDate = LocalDateTime.now();
        Receipt receipt = new Receipt(1001, receiptDate, expectedStoreName);

        assertEquals(expectedStoreName, receipt.getStoreName());
    }

    @Test
    void testGetItemsFromBasket() {
        Basket basket = new Basket();
        Item item = new UnitPricedArticle("Coffee", 5000, Vat.FOOD, 101);
        basket.addItem(item);

        LocalDateTime receiptDate = LocalDateTime.now();
        Receipt receipt = new Receipt(1001, receiptDate, "Java Craftsman Store", basket);

        List<Item> items = receipt.getItems();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Coffee", items.get(0).getName());
    }


}
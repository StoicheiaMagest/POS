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
        Basket basket = new Basket();
        Receipt receipt = new Receipt(expectedReceiptNumber, receiptDate, "storeName", basket);

        assertEquals(expectedReceiptNumber, receipt.getReceiptNumber());
    }

    @Test
    void testGetDateTime() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 6, 1, 12, 30);
        Basket basket = new Basket();
        Receipt receipt = new Receipt(1001, expectedDateTime, "storeName", basket);

        assertEquals(expectedDateTime, receipt.getReceiptDateAndTime());
    }

    @Test
    void testGetStoreName() {
        String expectedStoreName = "Java Craftsman Store";
        LocalDateTime receiptDate = LocalDateTime.now();
        Basket basket = new Basket();
        Receipt receipt = new Receipt(1001, receiptDate, expectedStoreName, basket);

        assertEquals(expectedStoreName, receipt.getStoreName());
    }

    @Test
    void testPrintReceiptWithItems() {
        Basket basket = new Basket();
        Item item = new UnitPricedArticle("Coffee", 2500, Vat.FOOD, 1);
        basket.addItem(item);

        LocalDateTime receiptDate = LocalDateTime.of(2026, 6, 1, 12, 30);
        Receipt receipt = new Receipt(1001, receiptDate, "Java Craftsman Store", basket);

        String printedReceipt = receipt.print();

        assertNotNull(printedReceipt);
        assertTrue(printedReceipt.contains("Java Craftsman Store"));
        assertTrue(printedReceipt.contains("Coffee"));
    }


}
package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTestOld {

    @Test
    void testGetReceiptNumber() {
        int expectedReceiptNumber = 1001;
        LocalDateTime receiptDate = LocalDateTime.now();
        Basket basket = new Basket();
        ReceiptOld receiptOld = new ReceiptOld(expectedReceiptNumber, receiptDate, "storeName", basket);

        assertEquals(expectedReceiptNumber, receiptOld.getReceiptNumber());
    }

    @Test
    void testGetDateTime() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 6, 1, 12, 30);
        Basket basket = new Basket();
        ReceiptOld receiptOld = new ReceiptOld(1001, expectedDateTime, "storeName", basket);

        assertEquals(expectedDateTime, receiptOld.getReceiptDateAndTime());
    }

    @Test
    void testGetStoreName() {
        String expectedStoreName = "Java Craftsman Store";
        LocalDateTime receiptDate = LocalDateTime.now();
        Basket basket = new Basket();
        ReceiptOld receiptOld = new ReceiptOld(1001, receiptDate, expectedStoreName, basket);

        assertEquals(expectedStoreName, receiptOld.getStoreName());
    }

    @Test
    void testPrintReceiptWithItems() {
        Basket basket = new Basket();
        Item item = new UnitPricedArticle("Coffee", 2500, Vat.FOOD, 1);
        basket.addItem(item);

        LocalDateTime receiptDate = LocalDateTime.of(2026, 6, 1, 12, 30);
        ReceiptOld receiptOld = new ReceiptOld(1001, receiptDate, "Java Craftsman Store", basket);

        String printedReceipt = receiptOld.print();

        assertNotNull(printedReceipt);
        assertTrue(printedReceipt.contains("Java Craftsman Store"));
        assertTrue(printedReceipt.contains("Coffee"));
    }


}
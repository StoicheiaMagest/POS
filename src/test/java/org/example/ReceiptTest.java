package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
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
}
package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {

    @Test
    void testGetReceiptNumber() {
        int expectedReceiptNumber = 1001;
        LocalDateTime receiptDate = LocalDateTime.now();
        Receipt receipt = new Receipt(expectedReceiptNumber, receiptDate);

        assertEquals(expectedReceiptNumber, receipt.getReceiptNumber());
    }

    @Test
    void testGetDateTime() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 6, 1, 12, 30);
        Receipt receipt = new Receipt(1001, expectedDateTime);

        assertEquals(expectedDateTime, receipt.getReceiptDateAndTime());
    }
}
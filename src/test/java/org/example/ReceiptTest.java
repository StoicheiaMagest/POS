package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {

    @Test
    void testGetReceiptNumber() {
        int expectedReceiptNumber = 1001;
        Receipt receipt = new Receipt(expectedReceiptNumber);

        assertEquals(expectedReceiptNumber, receipt.getReceiptNumber());
    }
}
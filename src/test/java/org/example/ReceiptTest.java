package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ReceiptTest {

    int receiptNumber;
    LocalDateTime receiptDate;
    String storeName;
    PurchaseOccasion purchaseOccasion;
    Receipt receipt;

    @BeforeEach
    void setUp() {
        receiptNumber = 1;
        receiptDate = LocalDateTime.of(2026, 10, 23, 13, 3);
        storeName = "My Store";
        purchaseOccasion = mock(PurchaseOccasion.class);
        receipt = new Receipt(receiptNumber, receiptDate, storeName, purchaseOccasion);
    }

    @Test
    void testGetStoreName() {
        assertEquals(storeName, receipt.getStoreName());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", "\u2003", "\r", "\r\n", "\f",
            "\u000B", "\u001C", "\u001D", "\u001E", "\u001F"})
    void testBlankStoreNameThrows(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new Receipt(receiptNumber, receiptDate, name, purchaseOccasion));
    }

    @ParameterizedTest
    @NullSource
    void testNullStoreNameThrows(String name) {
        assertThrows(NullPointerException.class,
                () -> new Receipt(receiptNumber, receiptDate, name, purchaseOccasion));
    }

    @Test
    void testGetReceiptNumber() {
        assertEquals(receiptNumber, receipt.getReceiptNumber());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void testNonPositiveReceiptNumberThrows(int number) {
        assertThrows(IllegalArgumentException.class,
                () -> new Receipt(number, receiptDate, storeName, purchaseOccasion));
    }

    @Test
    void testGetReceiptDate() {
        assertEquals(receiptDate, receipt.getReceiptDate());
    }

    @Test
    void testNullDateThrows() {
        assertThrows(NullPointerException.class,
                () -> new Receipt(receiptNumber, null, storeName, purchaseOccasion));
    }

    @Test
    void testGetPurchaseOccasion() {
        assertEquals(purchaseOccasion, receipt.getPurchaseOccasion());
    }

    @Test
    void testNullPurchaseOccasionThrows() {
        assertThrows(NullPointerException.class,
                () -> new Receipt(receiptNumber, receiptDate, storeName, null));
    }

    @Test
    void testGetFormattedDate(){
        assertEquals("2026-10-23 13:03", receipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateSingleHourDigit() {
        LocalDateTime morning = LocalDateTime.of(2026, 10, 23, 9, 3);
        Receipt morningReceipt = new Receipt(receiptNumber, morning, storeName, purchaseOccasion);
        assertEquals("2026-10-23 09:03", morningReceipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateFirstDayOfTheMonth() {
        LocalDateTime firstDate = LocalDateTime.of(2026, 10, 1, 13, 30);
        Receipt firstDateReceipt = new Receipt(receiptNumber, firstDate, storeName, purchaseOccasion);
        assertEquals("2026-10-01 13:30", firstDateReceipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateFirstMonthOfYear() {
        LocalDateTime firstMonth = LocalDateTime.of(2026, 1, 1, 13, 30);
        Receipt firstMonthReceipt = new Receipt(receiptNumber, firstMonth, storeName, purchaseOccasion);
        assertEquals("2026-01-01 13:30", firstMonthReceipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateZeroHour() {
        LocalDateTime zeroHour = LocalDateTime.of(2026, 1, 1, 0, 30);
        Receipt zeroHourReceipt = new Receipt(receiptNumber, zeroHour, storeName, purchaseOccasion);
        assertEquals("2026-01-01 00:30", zeroHourReceipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateZeroMinute() {
        LocalDateTime zeroMinute = LocalDateTime.of(2026, 1, 1, 13, 0);
        Receipt zeroMinuteReceipt = new Receipt(receiptNumber, zeroMinute, storeName, purchaseOccasion);
        assertEquals("2026-01-01 13:00", zeroMinuteReceipt.getFormattedDate());
    }

    @Test
    void testGetFormattedDateDropsSeconds() {
        LocalDateTime withSeconds = LocalDateTime.of(2026, 10, 23, 13, 3, 59);
        Receipt withSecondsReceipt = new Receipt(receiptNumber, withSeconds, storeName, purchaseOccasion);
        assertEquals("2026-10-23 13:03", withSecondsReceipt.getFormattedDate());
    }

    @Test
    void testPrintEmptyBasket() {
        when(purchaseOccasion.getBasket()).thenReturn(new Basket());
        String expected = "My Store\n" +
                "Receipt number: 1\n" +
                "Date: 2026-10-23 13:03\n" +
                "Total: 0,00 SEK";
        assertEquals(expected, receipt.print());
    }

    @Test
    void testPrintOneItem() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Milk", 1000, Vat.FOOD, 111));
        when(purchaseOccasion.getBasket()).thenReturn(basket);
        String expected = "My Store\n" +
                "Receipt number: 1\n" +
                "Date: 2026-10-23 13:03\n" +
                "Milk 11,20 SEK\n" +
                "Total: 11,20 SEK";
        assertEquals(expected, receipt.print());
    }

    @Test
    void testToString() {
        when(purchaseOccasion.toString()).thenReturn("Occasion X");
        String expectedToString = "Receipt{receiptNumber=1, receiptDate=2026-10-23T13:03," +
                " storeName=My Store, purchaseOccasion=Occasion X}";
        assertEquals(expectedToString, receipt.toString());
    }

}
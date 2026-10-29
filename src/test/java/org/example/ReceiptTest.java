package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    void testToString() {
        when(purchaseOccasion.toString()).thenReturn("Occasion X");
        String expectedToString = "Receipt{receiptNumber=1, receiptDate=2026-10-23T13:03," +
                " storeName=My Store, purchaseOccasion=Occasion X}";
        assertEquals(expectedToString, receipt.toString());
    }

    // ---- getReceiptText ----
    // The text is lines joined with "\n", with no trailing newline. Every line is
    // at most 42 characters wide, amounts use a comma and are right-aligned.

    private static final String EQ = "=".repeat(42);
    private static final String DASH = "-".repeat(42);
    private static final int FIRST_ITEM_LINE = 6;

    private Item item(String name, long priceInCents) {
        Item item = mock(Item.class);
        when(item.getName()).thenReturn(name);
        when(item.getInclusivePrice()).thenReturn(priceInCents);
        return item;
    }

    private PurchaseOccasion occasionWith(List<Item> items, long subtotal, Map<String, Long> discounts,
                                          long couponTotal, long seniorAmount, long total, boolean paid) {
        PurchaseOccasion occasion = mock(PurchaseOccasion.class);
        when(occasion.getItems()).thenReturn(items);
        when(occasion.getSubtotal()).thenReturn(subtotal);
        when(occasion.getDiscounts()).thenReturn(discounts);
        when(occasion.getCouponDiscountTotal()).thenReturn(couponTotal);
        when(occasion.getSeniorDiscountAmount()).thenReturn(seniorAmount);
        when(occasion.getTotalPriceForPurchase()).thenReturn(total);
        when(occasion.isPaid()).thenReturn(paid);
        return occasion;
    }

    // no discounts, not paid
    private PurchaseOccasion simpleOccasion(Item... items) {
        long sum = Arrays.stream(items).mapToLong(Item::getInclusivePrice).sum();
        return occasionWith(List.of(items), sum, Map.of(), 0L, 0L, sum, false);
    }

    // 131,60 subtotal, one multi-item discount, coupon and senior discount, paid
    private PurchaseOccasion occasionWithAllDiscounts() {
        Item milk = item("Mjölk", 1590L);
        List<Item> items = List.of(milk, milk, milk, item("Bröd", 2900L), item("Kaffe", 5490L));
        return occasionWith(items, 13160L, Map.of("3FOR2 on Mjölk", 1590L), 2000L, 957L, 8613L, true);
    }

    private String[] linesOf(PurchaseOccasion occasion) {
        return new Receipt(receiptNumber, receiptDate, storeName, occasion).getReceiptText().split("\n", -1);
    }

    private String row(String label, String amount) {
        return label + " ".repeat(42 - label.length() - amount.length()) + amount;
    }

    private String lineStartingWith(String[] lines, String prefix) {
        return Arrays.stream(lines).filter(l -> l.startsWith(prefix)).findFirst().orElse(null);
    }

    private int indexOfLineStartingWith(String[] lines, String prefix) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    // header

    @Test
    void testReceiptTextStartsAndEndsWithEqualsLine() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertEquals(EQ, lines[0]);
        assertEquals(EQ, lines[lines.length - 1]);
    }

    @Test
    void testReceiptTextContainsReceiptNumberAndDate() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertEquals("Kvittonr:    1", lines[3]);
        assertEquals("Datum:       2026-10-23 13:03", lines[4]);
    }

    @Test
    void testStoreNameIsCentered() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertEquals(EQ, lines[0]);
        assertEquals(" ".repeat(17) + "My Store", lines[1]);
        assertEquals(EQ, lines[2]);
    }

    @Test
    void testStoreNameWithOddLengthIsCenteredRoundedDown() {
        Receipt shortNameReceipt = new Receipt(receiptNumber, receiptDate, "ICA",
                simpleOccasion(item("Bröd", 2900L)));

        String[] lines = shortNameReceipt.getReceiptText().split("\n", -1);

        assertEquals(" ".repeat(19) + "ICA", lines[1]);
    }

    @Test
    void testStoreNameLongerThanTheLineIsPrintedAsItIs() {
        String longName = "S".repeat(50);
        Receipt longNameReceipt = new Receipt(receiptNumber, receiptDate, longName,
                simpleOccasion(item("Bröd", 2900L)));

        String[] lines = longNameReceipt.getReceiptText().split("\n", -1);

        assertEquals(longName, lines[1]);
    }

    // item lines and amount formatting

    @Test
    void testItemLineHasNameLeftAndAmountRightAligned() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertEquals("Bröd" + " ".repeat(33) + "29,00", lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testEveryItemGetsItsOwnLineInOrder() {
        Item milk = item("Mjölk", 1590L);
        String[] lines = linesOf(simpleOccasion(milk, item("Bröd", 2900L), milk));

        assertEquals(row("Mjölk", "15,90"), lines[FIRST_ITEM_LINE]);
        assertEquals(row("Bröd", "29,00"), lines[FIRST_ITEM_LINE + 1]);
        assertEquals(row("Mjölk", "15,90"), lines[FIRST_ITEM_LINE + 2]);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "0|0,00", "5|0,05", "99|0,99", "100|1,00", "1590|15,90", "13160|131,60", "123456|1234,56"})
    void testAmountIsFormattedWithCommaAndTwoDecimals(long cents, String expected) {
        String[] lines = linesOf(simpleOccasion(item("Vara", cents)));

        assertEquals(row("Vara", expected), lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testItemWithoutNameGetsAnEmptyLabel() {
        String[] lines = linesOf(simpleOccasion(item(null, 2990L)));

        assertEquals(" ".repeat(37) + "29,90", lines[FIRST_ITEM_LINE]);
    }

    // cutting long names

    @Test
    void testNameThatExactlyFitsIsNotCut() {
        String name = "N".repeat(36);
        String[] lines = linesOf(simpleOccasion(item(name, 2990L)));

        assertEquals(name + " " + "29,90", lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testNameOneCharacterTooLongIsCutWithMarker() {
        String[] lines = linesOf(simpleOccasion(item("N".repeat(37), 2990L)));

        assertEquals("N".repeat(35) + "~" + " " + "29,90", lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testLongNameIsCutWithMarkerAndAmountStaysInLastColumn() {
        String[] lines = linesOf(simpleOccasion(item("Ekologisk havredryck med extra kalcium 1L", 2990L)));

        assertEquals("Ekologisk havredryck med extra kalc~ 29,90", lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testLongerAmountLeavesLessRoomForTheName() {
        String[] lines = linesOf(simpleOccasion(item("A".repeat(35), 123456L)));

        assertEquals("A".repeat(33) + "~" + " " + "1234,56", lines[FIRST_ITEM_LINE]);
    }

    @Test
    void testNoLineIsLongerThan42Characters() {
        PurchaseOccasion occasion = occasionWith(
                List.of(item("X".repeat(80), 123456L)), 123456L,
                Map.of("3FOR2 on " + "Y".repeat(60), 1590L), 2000L, 957L, 8613L, true);

        for (String line : linesOf(occasion)) {
            assertTrue(line.length() <= 42, "Line too long: " + line);
        }
    }

    // discount block

    @Test
    void testDiscountBlockShowsSubtotalAndEveryDiscountLine() {
        String[] lines = linesOf(occasionWithAllDiscounts());

        assertEquals(row("Delsumma", "131,60"), lineStartingWith(lines, "Delsumma"));
        assertEquals(row("3FOR2 on Mjölk", "-15,90"), lineStartingWith(lines, "3FOR2"));
        assertEquals(row("Kuponger", "-20,00"), lineStartingWith(lines, "Kuponger"));
        assertEquals(row("Seniorrabatt", "-9,57"), lineStartingWith(lines, "Seniorrabatt"));
    }

    @Test
    void testDiscountBlockIsHiddenWhenThereAreNoDiscounts() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertNull(lineStartingWith(lines, "Delsumma"));
        assertNull(lineStartingWith(lines, "Kuponger"));
        assertNull(lineStartingWith(lines, "Seniorrabatt"));
    }

    @Test
    void testCouponAndSeniorLinesAreHiddenWhenZeroButMultiItemDiscountIsShown() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Mjölk", 4770L)), 4770L,
                Map.of("3FOR2 on Mjölk", 1590L), 0L, 0L, 3180L, false);
        String[] lines = linesOf(occasion);

        assertNotNull(lineStartingWith(lines, "Delsumma"));
        assertNotNull(lineStartingWith(lines, "3FOR2"));
        assertNull(lineStartingWith(lines, "Kuponger"));
        assertNull(lineStartingWith(lines, "Seniorrabatt"));
    }

    @Test
    void testBlockIsShownForCouponAloneAndSeniorAloneIsHidden() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of(), 2000L, 0L, 900L, false);
        String[] lines = linesOf(occasion);

        assertEquals(row("Delsumma", "29,00"), lineStartingWith(lines, "Delsumma"));
        assertEquals(row("Kuponger", "-20,00"), lineStartingWith(lines, "Kuponger"));
        assertNull(lineStartingWith(lines, "Seniorrabatt"));
    }

    @Test
    void testBlockIsShownForSeniorDiscountAlone() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of(), 0L, 290L, 2610L, false);
        String[] lines = linesOf(occasion);

        assertEquals(row("Delsumma", "29,00"), lineStartingWith(lines, "Delsumma"));
        assertEquals(row("Seniorrabatt", "-2,90"), lineStartingWith(lines, "Seniorrabatt"));
        assertNull(lineStartingWith(lines, "Kuponger"));
    }

    @Test
    void testSmallDiscountKeepsLeadingZeroInFraction() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of("3FOR2 on Bröd", 5L), 0L, 0L, 2895L, false);
        String[] lines = linesOf(occasion);

        assertEquals(row("3FOR2 on Bröd", "-0,05"), lineStartingWith(lines, "3FOR2"));
    }

    @Test
    void testDiscountEntriesWithZeroValueAreSkipped() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of("3FOR2 on Mjölk", 0L, "4FOR3 on Bröd", 100L), 0L, 0L, 2800L, false);
        String[] lines = linesOf(occasion);

        assertNull(lineStartingWith(lines, "3FOR2"));
        assertNotNull(lineStartingWith(lines, "4FOR3"));
    }

    @Test
    void testDiscountBlockIsHiddenWhenOnlyZeroValueDiscountsExist() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of("3FOR2 on Mjölk", 0L), 0L, 0L, 2900L, false);
        String[] lines = linesOf(occasion);

        assertNull(lineStartingWith(lines, "Delsumma"));
        assertNull(lineStartingWith(lines, "3FOR2"));
    }

    @Test
    void testMultiItemDiscountsAreSortedByName() {
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of("4FOR3 on Bröd", 100L, "3FOR2 on Mjölk", 1590L), 0L, 0L, 1210L, false);
        String[] lines = linesOf(occasion);

        int first = indexOfLineStartingWith(lines, "3FOR2");
        int second = indexOfLineStartingWith(lines, "4FOR3");

        assertTrue(first >= 0 && second >= 0);
        assertTrue(first < second);
    }

    @Test
    void testLongDiscountNameIsCutLikeAnItemName() {
        String key = "3FOR2 on " + "X".repeat(41);
        PurchaseOccasion occasion = occasionWith(List.of(item("Bröd", 2900L)), 2900L,
                Map.of(key, 1590L), 0L, 0L, 1310L, false);
        String[] lines = linesOf(occasion);

        assertEquals("3FOR2 on " + "X".repeat(25) + "~" + " " + "-15,90", lineStartingWith(lines, "3FOR2"));
    }

    // total and paid line

    @Test
    void testTotalLineShowsTheTotalPrice() {
        String[] lines = linesOf(occasionWithAllDiscounts());

        assertEquals(row("SUMMA", "86,13"), lineStartingWith(lines, "SUMMA"));
    }

    @Test
    void testPaidLineIsShownWhenThePurchaseIsPaid() {
        String[] lines = linesOf(occasionWithAllDiscounts());

        assertEquals(EQ, lines[lines.length - 1]);
        assertEquals("BETALT", lines[lines.length - 2]);
        assertEquals(DASH, lines[lines.length - 3]);
    }

    @Test
    void testPaidLineIsHiddenWhenThePurchaseIsNotPaid() {
        String[] lines = linesOf(simpleOccasion(item("Bröd", 2900L)));

        assertNull(lineStartingWith(lines, "BETALT"));
    }

    // whole receipts

    @Test
    void testCompleteReceiptWithAllDiscountsAndPaid() {
        String expected = String.join("\n",
                "==========================================",
                "                 My Store",
                "==========================================",
                "Kvittonr:    1",
                "Datum:       2026-10-23 13:03",
                "------------------------------------------",
                "Mjölk                                15,90",
                "Mjölk                                15,90",
                "Mjölk                                15,90",
                "Bröd                                 29,00",
                "Kaffe                                54,90",
                "------------------------------------------",
                "Delsumma                            131,60",
                "3FOR2 on Mjölk                      -15,90",
                "Kuponger                            -20,00",
                "Seniorrabatt                         -9,57",
                "------------------------------------------",
                "SUMMA                                86,13",
                "------------------------------------------",
                "BETALT",
                "==========================================");

        Receipt fullReceipt = new Receipt(1, LocalDateTime.of(2026, 10, 23, 13, 3), "My Store",
                occasionWithAllDiscounts());

        assertEquals(expected, fullReceipt.getReceiptText());
    }

    @Test
    void testCompleteReceiptWithoutDiscountsAndNotPaid() {
        String expected = String.join("\n",
                "==========================================",
                "                 My Store",
                "==========================================",
                "Kvittonr:    2",
                "Datum:       2026-10-23 13:07",
                "------------------------------------------",
                "Bröd                                 29,00",
                "------------------------------------------",
                "SUMMA                                29,00",
                "==========================================");

        Receipt plainReceipt = new Receipt(2, LocalDateTime.of(2026, 10, 23, 13, 7), "My Store",
                simpleOccasion(item("Bröd", 2900L)));

        assertEquals(expected, plainReceipt.getReceiptText());
    }

    // the text is a snapshot taken when the receipt is created

    @Test
    void testReceiptTextDoesNotChangeWhenTheOccasionChangesLater() {
        PurchaseOccasion occasion = simpleOccasion(item("Bröd", 2900L));
        Receipt snapshotReceipt = new Receipt(receiptNumber, receiptDate, storeName, occasion);
        String before = snapshotReceipt.getReceiptText();

        when(occasion.getTotalPriceForPurchase()).thenReturn(1L);
        when(occasion.isPaid()).thenReturn(true);
        when(occasion.getItems()).thenReturn(List.of());

        assertEquals(before, snapshotReceipt.getReceiptText());
    }

}
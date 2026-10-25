package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterSystemTest2 {

    // Feeds the commands to RegisterSystem2 via System.in and returns everything it printed.
    private String run(String... commands) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            String input = String.join("\n", commands) + "\nEXIT\n";
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            RegisterSystem2.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    // The printed receipt without its date/time line, which differs between runs.
    private String receiptOf(String output) {
        int start = output.indexOf("Java Butiken");
        int end = output.indexOf("Totalt");
        String receipt = output.substring(start, output.indexOf('\n', end));
        return receipt.replaceFirst("\n[^\n]*kvitto", "\nkvitto");
    }

    // From the first milk line to the end of the Totalt line: the part LIST and PAY have in common.
    private String itemsToTotal(String output) {
        int start = output.indexOf("Milk  ");
        int end = output.indexOf("Totalt");
        return output.substring(start, output.indexOf('\n', end));
    }

    private int countOf(String text, String part) {
        int count = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) {
            count++;
        }
        return count;
    }

    @Test
    void newOrderWithoutMemberOrSeniorIsAnonymous() {
        String output = run("NEW", "SCAN 111");

        assertTrue(output.contains("New order started for an anonymous customer"));
        assertFalse(output.contains("New order started for Member"));
        assertFalse(output.contains("New order started for Senior"));
    }

    @Test
    void scanningItem111AfterNewAddsItToBasket() {
        String output = run("NEW", "SCAN 111");

        assertTrue(output.contains("Scanned 111"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void payingForItem111PrintsMilkOnReceipt() {
        String output = run("NEW", "SCAN 111", "PAY");

        assertTrue(output.contains("Milk  4,48 SEK"));
        assertTrue(output.contains("Totalt  4,48 SEK"));
    }

    @Test
    void listShowsItemsDiscountsAndTotalBeforePaying() {
        String output = run("NEW", "SCAN 111", "SCAN 222", "WEIGH 333 500", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\nApple  500 g  16,8 SEK\n"
                + "2 kr off on Bread  -2 SEK\nTotalt  20,96 SEK"));
        assertFalse(output.contains("Subtotal"));
    }

    @Test
    void listAppliesTheSameDiscountsAsPay() {
        String output = run("NEW", "SCAN 111", "SCAN 111", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nMilk  4,48 SEK\nMilk  4,48 SEK\nBread  1,68 SEK\n"
                + "3 for 2 Milk  -4,48 SEK\n2 kr off on Bread  -2 SEK\nTotalt  8,64 SEK"));
    }

    @Test
    void listMatchesWhatPayPrintsOnTheReceipt() {
        String[] order = {"NEW SENIOR", "SCAN 111", "SCAN 111", "SCAN 111", "SCAN 222"};
        String[] withList = java.util.Arrays.copyOf(order, order.length + 1);
        withList[order.length] = "LIST";
        String[] withPay = java.util.Arrays.copyOf(order, order.length + 1);
        withPay[order.length] = "PAY";

        assertEquals(itemsToTotal(run(withPay)), itemsToTotal(run(withList)));
    }

    @Test
    void listDoesNotShowRemovedItem() {
        String output = run("NEW", "SCAN 111", "SCAN 222", "REMOVE 111", "LIST");

        assertTrue(output.contains("Bread  1,68 SEK\n2 kr off on Bread  -2 SEK\nTotalt  0 SEK"));
        assertFalse(output.contains("Milk  4,48 SEK"));
    }

    @Test
    void listWithoutNewGivesError() {
        String output = run("LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Totalt"));
    }

    @Test
    void listOnEmptyBasketShowsNoItemsAndZeroTotal() {
        String output = run("NEW", "LIST");

        assertTrue(output.contains("Totalt  0 SEK"));
        assertFalse(output.contains("Milk"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void listDoesNotChangeTheBasket() {
        String withList = run("NEW", "SCAN 111", "SCAN 222", "LIST", "LIST", "PAY");
        String withoutList = run("NEW", "SCAN 111", "SCAN 222", "PAY");

        assertEquals(receiptOf(withoutList), receiptOf(withList));
    }

    @Test
    void listDoesNotPayOrPrintAReceipt() {
        String listOnly = run("NEW", "SCAN 111", "LIST");
        String listThenPay = run("NEW", "SCAN 111", "LIST", "PAY");

        assertFalse(listOnly.contains("Java Butiken"));
        assertFalse(listOnly.contains("kvitto"));
        // the receipt for the real payment is still number 1 and printed once
        assertEquals(1, countOf(listThenPay, "Java Butiken"));
        assertTrue(listThenPay.contains("kvitto 1"));
    }

    @Test
    void listShowsItemsInTheOrderTheyWereAdded() {
        String output = run("NEW", "WEIGH 333 500", "SCAN 222", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Apple  500 g  16,8 SEK\nBread  1,68 SEK\nMilk  4,48 SEK\nBread  1,68 SEK\n"
                + "2 kr off on Bread  -4 SEK\nTotalt  20,64 SEK"));
    }

    @Test
    void listAfterPaymentGivesErrorUntilNewOrderIsStarted() {
        String output = run("NEW", "SCAN 111", "PAY", "LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        // only the receipt from PAY has a Totalt line, LIST printed none
        assertEquals(1, countOf(output, "Totalt"));
    }

    @Test
    void listAfterNewOrderOnlyShowsTheNewOrdersItems() {
        String output = run("NEW", "SCAN 111", "NEW", "SCAN 222", "LIST");

        assertTrue(output.contains("Bread  1,68 SEK\n2 kr off on Bread  -2 SEK\nTotalt  0 SEK"));
        assertFalse(output.contains("Milk  4,48 SEK"));
    }

    @Test
    void listForMemberShowsItemDiscountsButNoSeniorDiscount() {
        String output = run("NEW MEMBER", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\n2 kr off on Bread  -2 SEK\nTotalt  4,16 SEK"));
        assertFalse(output.contains("Senior 10%"));
    }

    @Test
    void listForSeniorIncludesTheSeniorDiscount() {
        String output = run("NEW SENIOR", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\n2 kr off on Bread  -2 SEK\n"
                + "Senior 10%  -0,42 SEK\nTotalt  3,74 SEK"));
    }

    @Test
    void listIgnoresCaseAndSurroundingSpaces() {
        String lower = run("NEW", "SCAN 111", "list");
        String padded = run("NEW", "SCAN 111", "  LIST  ");

        assertTrue(lower.contains("Milk  4,48 SEK\nTotalt  4,48 SEK"));
        assertTrue(padded.contains("Milk  4,48 SEK\nTotalt  4,48 SEK"));
    }

    @Test
    void listFormatsWeighedItemsOfDifferentWeights() {
        String output = run("NEW", "WEIGH 333 1", "WEIGH 333 250", "WEIGH 333 1000", "LIST");

        assertTrue(output.contains(
                "Apple  1 g  0,03 SEK\nApple  250 g  8,4 SEK\nApple  1000 g  33,6 SEK\nTotalt  42,03 SEK"));
    }

    @Test
    void listIgnoresExtraArguments() {
        String output = run("NEW", "SCAN 111", "LIST 111");

        assertTrue(output.contains("Milk  4,48 SEK\nTotalt  4,48 SEK"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void scanningItem111WithoutNewGivesError() {
        String output = run("SCAN 111");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Scanned 111"));
    }
}

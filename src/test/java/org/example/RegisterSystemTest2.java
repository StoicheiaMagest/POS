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
    void listShowsAllItemsInBasketBeforePaying() {
        String output = run("NEW", "SCAN 111", "SCAN 222", "WEIGH 333 500", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\nApple  500 g  16,8 SEK\nSubtotal  22,96 SEK"));
        assertFalse(output.contains("Totalt"));
    }

    @Test
    void listDoesNotShowRemovedItem() {
        String output = run("NEW", "SCAN 111", "SCAN 222", "REMOVE 111", "LIST");

        assertTrue(output.contains("Bread  1,68 SEK\nSubtotal  1,68 SEK"));
        assertFalse(output.contains("Milk  4,48 SEK"));
    }

    @Test
    void listWithoutNewGivesError() {
        String output = run("LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Subtotal"));
    }

    @Test
    void listOnEmptyBasketShowsNoItemsAndZeroSubtotal() {
        String output = run("NEW", "LIST");

        assertTrue(output.contains("Subtotal  0 SEK"));
        assertFalse(output.contains("Milk"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void listDoesNotApplyDiscounts() {
        // 3 for 2 on milk and 2 kr off on bread would both trigger at PAY
        String output = run("NEW", "SCAN 111", "SCAN 111", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nMilk  4,48 SEK\nMilk  4,48 SEK\nBread  1,68 SEK\nSubtotal  15,12 SEK"));
        assertFalse(output.contains("3 for 2"));
        assertFalse(output.contains("2 kr off"));
    }

    @Test
    void listDoesNotChangeTheBasket() {
        String withList = run("NEW", "SCAN 111", "SCAN 222", "LIST", "LIST", "PAY");
        String withoutList = run("NEW", "SCAN 111", "SCAN 222", "PAY");

        assertEquals(receiptOf(withoutList), receiptOf(withList));
    }

    @Test
    void listShowsItemsInTheOrderTheyWereAdded() {
        String output = run("NEW", "WEIGH 333 500", "SCAN 222", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains(
                "Apple  500 g  16,8 SEK\nBread  1,68 SEK\nMilk  4,48 SEK\nBread  1,68 SEK\nSubtotal  24,64 SEK"));
    }

    @Test
    void listAfterPaymentGivesErrorUntilNewOrderIsStarted() {
        String output = run("NEW", "SCAN 111", "PAY", "LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Subtotal"));
    }

    @Test
    void listAfterNewOrderOnlyShowsTheNewOrdersItems() {
        String output = run("NEW", "SCAN 111", "NEW", "SCAN 222", "LIST");

        assertTrue(output.contains("Bread  1,68 SEK\nSubtotal  1,68 SEK"));
        assertFalse(output.contains("Milk  4,48 SEK"));
    }

    @Test
    void listForMemberShowsSameUndiscountedPricesAsAnonymous() {
        String output = run("NEW MEMBER", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\nSubtotal  6,16 SEK"));
        assertFalse(output.contains("%"));
    }

    @Test
    void listForSeniorShowsSameUndiscountedPricesAsAnonymous() {
        String output = run("NEW SENIOR", "SCAN 111", "SCAN 222", "LIST");

        assertTrue(output.contains("Milk  4,48 SEK\nBread  1,68 SEK\nSubtotal  6,16 SEK"));
        assertFalse(output.contains("Senior 10%"));
    }

    @Test
    void listIgnoresCaseAndSurroundingSpaces() {
        String lower = run("NEW", "SCAN 111", "list");
        String padded = run("NEW", "SCAN 111", "  LIST  ");

        assertTrue(lower.contains("Milk  4,48 SEK\nSubtotal  4,48 SEK"));
        assertTrue(padded.contains("Milk  4,48 SEK\nSubtotal  4,48 SEK"));
    }

    @Test
    void listFormatsWeighedItemsOfDifferentWeights() {
        String output = run("NEW", "WEIGH 333 1", "WEIGH 333 250", "WEIGH 333 1000", "LIST");

        assertTrue(output.contains(
                "Apple  1 g  0,03 SEK\nApple  250 g  8,4 SEK\nApple  1000 g  33,6 SEK\nSubtotal  42,03 SEK"));
    }

    @Test
    void listIgnoresExtraArguments() {
        String output = run("NEW", "SCAN 111", "LIST 111");

        assertTrue(output.contains("Milk  4,48 SEK\nSubtotal  4,48 SEK"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void scanningItem111WithoutNewGivesError() {
        String output = run("SCAN 111");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Scanned 111"));
    }
}

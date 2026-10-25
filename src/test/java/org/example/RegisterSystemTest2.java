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
    void scanningItem111WithoutNewGivesError() {
        String output = run("SCAN 111");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Scanned 111"));
    }
}

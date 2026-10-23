package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasketCliTest {

    ArticleRegistry catalog;
    BasketCli cli;

    @BeforeEach
    void setUp() {
        catalog = new ArticleRegistry();
        catalog.addItem(111, new UnitPricedArticle("Milk", 1000, Vat.FOOD, 111));
        catalog.addItem(222, new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));
        catalog.addItem(333, new WeighedItem("Apples", 2990, Vat.FOOD, 0, 333));
    }

    // Runs the CLI with the given input lines and returns everything it printed.
    private String run(String... lines) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        cli = new BasketCli(catalog,
                new StringReader(String.join("\n", lines) + "\n"),
                new PrintStream(bytes));
        cli.run();
        return bytes.toString();
    }

    @Test
    void quitStopsTheLoop() {
        String output = run("quit", "help");

        assertTrue(output.contains("Bye"));
        assertEquals(output.indexOf("Commands:"), output.lastIndexOf("Commands:"),
                "help after quit must not run");
    }

    @Test
    void helpListsAllCommands() {
        String output = run("help", "quit");

        for (String command : new String[]{"catalog", "add", "list", "update", "remove", "receipt", "quit"}) {
            assertTrue(output.contains(command), "help should mention " + command);
        }
    }

    @Test
    void unknownCommandIsReported() {
        assertTrue(run("dance", "quit").contains("Unknown command: dance"));
    }

    @Test
    void blankLinesAreIgnored() {
        assertTrue(!run("", "   ", "quit").contains("Unknown command"));
    }

    @Test
    void endOfInputEndsTheLoop() {
        assertTrue(run("help").contains("Bye"));
    }

    @Test
    void commandsAreCaseInsensitive() {
        String output = run("HELP", "QUIT");

        assertEquals(2, output.split("Commands:", -1).length - 1,
                "banner plus the HELP output");
    }

    @Test
    void catalogListsAllItemsWithPrices() {
        String output = run("catalog", "quit");

        assertTrue(output.contains("111  Milk  11,20 SEK"));
        assertTrue(output.contains("222  Bread  22,40 SEK"));
        assertTrue(output.contains("333  Apples  29,90 SEK/kg"));
    }
}

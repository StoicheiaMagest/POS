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

    @Test
    void addPutsOneUnitItemInTheBasket() {
        String output = run("add 111", "quit");

        assertEquals(1, cli.getBasket().getItems().size());
        assertEquals("Milk", cli.getBasket().getItems().get(0).getName());
        assertTrue(output.contains("Added 1 x Milk"));
    }

    @Test
    void addWithQuantityPutsSeveralItemsInTheBasket() {
        String output = run("add 111 3", "quit");

        assertEquals(3, cli.getBasket().getItems().size());
        assertTrue(output.contains("Added 3 x Milk"));
    }

    @Test
    void addWeighedItemUsesTheGivenGrams() {
        String output = run("add 333 520", "quit");

        WeighedItem apples = (WeighedItem) cli.getBasket().getItems().get(0);
        assertEquals(520, apples.getWeight());
        assertEquals(2990, apples.getPricePerKg());
        assertTrue(output.contains("Added Apples 0,520 kg"));
    }

    @Test
    void addWeighedItemWithoutGramsIsRejected() {
        String output = run("add 333", "quit");

        assertEquals(0, cli.getBasket().getItems().size());
        assertTrue(output.contains("Apples is sold by weight, give the weight in grams"));
    }

    @Test
    void addUnknownArticleIsRejected() {
        String output = run("add 999", "quit");

        assertEquals(0, cli.getBasket().getItems().size());
        assertTrue(output.contains("No such article: 999"));
    }

    @Test
    void addWithNonNumericArgumentsIsRejected() {
        assertTrue(run("add abc", "quit").contains("Not a number: abc"));
        assertTrue(run("add 111 two", "quit").contains("Not a number: two"));
    }

    @Test
    void addWithoutArticleShowsUsage() {
        assertTrue(run("add", "quit").contains("Usage: add <article> [qty|grams]"));
    }

    @Test
    void addWithNonPositiveQuantityIsRejected() {
        assertTrue(run("add 111 0", "quit").contains("must be greater than 0"));
        assertTrue(run("add 111 -2", "quit").contains("must be greater than 0"));
        assertEquals(0, cli.getBasket().getItems().size());
    }
}

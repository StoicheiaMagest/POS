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

    @Test
    void listOfEmptyBasket() {
        assertTrue(run("list", "quit").contains("Basket is empty"));
    }

    @Test
    void listNumbersTheItemsAndShowsTheTotal() {
        String output = run("add 111", "add 333 520", "list", "quit");

        assertTrue(output.contains("1  Milk  11,20 SEK"));
        assertTrue(output.contains("2  Apples  0,520 kg  17,40 SEK"));
        assertTrue(output.contains("Total: 28,60 SEK"));
    }

    @Test
    void removeTakesTheNumberedItemOutOfTheBasket() {
        String output = run("add 111", "add 222", "remove 1", "list", "quit");

        assertEquals(1, cli.getBasket().getItems().size());
        assertEquals("Bread", cli.getBasket().getItems().get(0).getName());
        assertTrue(output.contains("Removed Milk"));
        assertTrue(output.contains("1  Bread  22,40 SEK"));
    }

    @Test
    void removeOneOfSeveralIdenticalItems() {
        run("add 111 3", "remove 2", "quit");

        assertEquals(2, cli.getBasket().getItems().size());
    }

    @Test
    void removeWithNumberOutsideTheBasketIsRejected() {
        String output = run("add 111", "remove 2", "remove 0", "quit");

        assertEquals(1, cli.getBasket().getItems().size());
        assertTrue(output.contains("No item number 2 in the basket"));
        assertTrue(output.contains("No item number 0 in the basket"));
    }

    @Test
    void removeWithBadArgumentsIsRejected() {
        assertTrue(run("remove", "quit").contains("Usage: remove <no>"));
        assertTrue(run("remove x", "quit").contains("Not a number: x"));
    }

    @Test
    void updateChangesTheWeightAndKeepsThePosition() {
        String output = run("add 111", "add 333 520", "add 222", "update 2 700", "list", "quit");

        assertTrue(output.contains("Updated Apples to 0,700 kg"));
        assertTrue(output.contains("1  Milk  11,20 SEK"));
        assertTrue(output.contains("2  Apples  0,700 kg  23,44 SEK"));
        assertTrue(output.contains("3  Bread  22,40 SEK"));
        assertEquals(3, cli.getBasket().getItems().size());
    }

    @Test
    void updateOfAUnitItemIsRejected() {
        String output = run("add 111", "update 1 500", "quit");

        assertTrue(output.contains("Milk is not sold by weight, only weighed items can be updated"));
    }

    @Test
    void updateWithBadInputIsRejected() {
        assertTrue(run("update", "quit").contains("Usage: update <no> <grams>"));
        assertTrue(run("update 1 100", "quit").contains("No item number 1 in the basket"));
        assertTrue(run("add 333 500", "update 1 0", "quit").contains("Weight must be greater than 0"));
        assertTrue(run("add 333 500", "update 1 heavy", "quit").contains("Not a number: heavy"));
    }

    @Test
    void receiptPrintsTheBasketWithDiscountsAndVat() {
        String output = run("add 111 3", "add 333 520", "receipt", "quit");

        assertTrue(output.contains("Test Store"));
        assertTrue(output.contains("Receipt number: 1"));
        assertTrue(output.contains("Milk 3 x 11,20 = 33,60 SEK"));
        assertTrue(output.contains("Apples 0,520 kg x 29,90 = 17,40 SEK"));
        assertTrue(output.contains("3 for 2 Milk -11,20 SEK"));
        assertTrue(output.contains("VAT 12,00%: 4,26 SEK"));
        assertTrue(output.contains("Total: 39,80 SEK"));
    }

    @Test
    void receiptNumbersCountUp() {
        String output = run("add 111", "receipt", "receipt", "quit");

        assertTrue(output.contains("Receipt number: 1"));
        assertTrue(output.contains("Receipt number: 2"));
    }

    @Test
    void receiptOfEmptyBasketIsRefused() {
        String output = run("receipt", "quit");

        assertTrue(output.contains("Basket is empty, nothing to print"));
        assertTrue(!output.contains("Receipt number"));
    }
}

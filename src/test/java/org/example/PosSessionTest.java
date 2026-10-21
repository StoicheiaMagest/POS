package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the command session: dispatch conventions of spec &sect;2 —
 * case-insensitivity (C1), unknown commands (C5), help listing (&sect;4.2)
 * and exit (&sect;4.17).
 */
public class PosSessionTest {

    @Test
    void unknownCommandGetsHintAndKeepsSessionAlive() {
        PosSession session = newSession();

        String out = session.execute("frobnicate the widget");

        assertEquals("Unknown command. Type 'help'.", out);
        assertTrue(session.isRunning());
    }

    @Test
    void commandsAreCaseInsensitiveWithSurroundingWhitespace() {
        PosSession session = newSession();

        String out = session.execute("  ExIt  ");

        assertEquals("Goodbye!", out);
        assertTrue(!session.isRunning());
    }

    @Test
    void helpListsEveryCommand() {
        PosSession session = newSession();
        String[] commands = {"enter store", "run pos", "create basket",
                "list store-items", "add", "remove", "list basket-items",
                "to pos", "to cash-register", "scan items", "apply discount",
                "register membership", "payment", "use card", "create card",
                "help", "exit"};

        String out = session.execute("HELP");

        for (String command : commands) {
            assertTrue(out.contains(command), "help misses: " + command);
        }
    }

    private PosSession newSession() {
        return new PosSession(ArticleRegistry.withStandardAssortment(),
                new java.util.Random(1));
    }

    @Test
    void enterStoreGreetsAndCreateBasketWorks() {
        PosSession session = newSession();

        String entered = session.execute("enter store");
        String created = session.execute("create basket");

        assertEquals("Welcome to Kwik E-Smart! Go ahead and pick your items.",
                entered);
        assertEquals("Basket has been created", created);
    }

    @Test
    void createBasketBeforeEnteringStoreIsRefused() {
        PosSession session = newSession();

        String out = session.execute("create basket");

        assertTrue(out.contains("enter store"),
                "must hint the command to run first: " + out);
        assertTrue(!out.equals("Basket has been created"));
    }

    @Test
    void enteringStoreTwiceStartsAFreshVisit() {
        PosSession session = newSession();
        session.execute("enter store");

        String again = session.execute("enter store");

        assertEquals("Welcome to Kwik E-Smart! Go ahead and pick your items.",
                again);
    }

    @Test
    void listStoreItemsShowsTwelveArticlesWithFourColumns() {
        // Arrange: spec §5 handles with their inclusive display prices
        String[][] expected = {
            {"soy-milk", "Alpro Soymilk", "34.96 SEK"},
            {"oat-drink", "Alpro Oat Drink", "32.95 SEK"},
            {"orange-juice", "Tropicana Orange Juice", "29.95 SEK"},
            {"hummus", "Hummus Original 200g", "24.95 SEK"},
            {"tofu", "Organic Firm Tofu", "27.96 SEK"},
            {"black-beans", "Black Beans 400g", "19.95 SEK"},
            {"dark-chocolate", "Maraboo Dark Chocolate 100g", "22.95 SEK"},
            {"candle", "Scented Candle Vanilla", "49.95 SEK"},
            {"apple", "Apple", "24.90 SEK"},
            {"banana", "Banana", "15.90 SEK"},
            {"vegan-cheese", "Vegan Cheese Slices", "199.00 SEK"},
            {"olives", "Marinated Olives", "149.50 SEK"}
        };
        PosSession session = newSession();
        session.execute("enter store");

        String out = session.execute("list store-items");

        assertTrue(out.contains("Name"), out);
        assertTrue(out.contains("Full Description"), out);
        assertTrue(out.contains("Unit"), out);
        assertTrue(out.contains("Price"), out);
        for (String[] row : expected) {
            assertTrue(out.contains(row[0]), "missing row: " + row[0]);
            assertTrue(out.contains(row[1]), "missing description: " + row[1]);
            assertTrue(out.contains(row[2]), "missing price: " + row[2]);
        }
        assertTrue(out.contains("1 pc"), out);
        assertTrue(out.contains("per kg"), out);
    }

    @Test
    void listStoreItemsBeforeEnteringStoreIsRefused() {
        PosSession session = newSession();

        String out = session.execute("list store-items");

        assertTrue(out.contains("enter store"), out);
    }
}

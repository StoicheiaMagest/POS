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
}

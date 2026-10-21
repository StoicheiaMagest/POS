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

    @Test
    void addUnitArticlePrintsConfirmationAndWeighedNeedsGrams() {
        PosSession session = enteredStoreWithBasket();

        String added = session.execute("add soy-milk");
        String weighed = session.execute("add apple 300");

        assertEquals("1 Alpro Soymilk added to basket", added);
        assertEquals("300g Apple added to basket", weighed);
    }

    @Test
    void addRejectsUnknownHandleAndBadQuantities() {
        PosSession session = enteredStoreWithBasket();

        assertTrue(session.execute("add socks").contains("Unknown item"));
        assertTrue(session.execute("add soy-milk 3").length() > 0);
        assertTrue(!session.execute("add soy-milk 3")
                .contains("added to basket"));
        assertTrue(!session.execute("add apple").contains("added to basket"));
        assertTrue(!session.execute("add apple -5")
                .contains("added to basket"));
        assertTrue(!session.execute("add apple abc")
                .contains("added to basket"));
    }

    @Test
    void addBeforeCreateBasketIsRefused() {
        PosSession session = newSession();
        session.execute("enter store");

        String out = session.execute("add soy-milk");

        assertTrue(out.contains("create basket"), out);
    }

    @Test
    void removeTakesExactlyOneAndReportsAbsence() {
        PosSession session = enteredStoreWithBasket();
        session.execute("add soy-milk");
        session.execute("add soy-milk");

        String removed = session.execute("remove soy-milk");
        String absent = session.execute("remove olives");

        assertEquals("One Alpro Soymilk has been removed from basket",
                removed);
        assertEquals("That item is not in your basket.", absent);
    }

    @Test
    void listBasketItemsGroupsAndShowsWeights() {
        PosSession session = enteredStoreWithBasket();
        session.execute("add soy-milk");
        session.execute("add orange-juice");
        session.execute("add orange-juice");
        session.execute("add apple 300");

        String out = session.execute("list basket-items");

        assertTrue(out.contains("The basket has the following items:"), out);
        assertTrue(out.contains("1"), out);
        assertTrue(out.contains("Alpro Soymilk"), out);
        assertTrue(out.contains("2"), out);
        assertTrue(out.contains("Tropicana Orange Juice"), out);
        assertTrue(out.contains("300g"), out);
        assertTrue(out.contains("Apple"), out);
    }

    @Test
    void emptyBasketListingSaysSo() {
        PosSession session = enteredStoreWithBasket();

        assertEquals("Your basket is empty.",
                session.execute("list basket-items"));
    }

    private PosSession enteredStoreWithBasket() {
        PosSession session = newSession();
        session.execute("enter store");
        session.execute("create basket");
        return session;
    }

    @Test
    void toPosMovesCustomerToRegisterAndBlocksBasketCommands() {
        PosSession session = enteredStoreWithBasket();
        session.execute("add soy-milk");

        String moved = session.execute("to pos");
        String blocked = session.execute("add oat-drink");

        assertEquals("At store register. You can now scan the items or "
                + "register membership", moved);
        assertTrue(blocked.contains("register"), blocked);
    }

    @Test
    void cashRegisterAliasBehavesIdentically() {
        PosSession session = enteredStoreWithBasket();

        String moved = session.execute("TO CASH-REGISTER");

        assertEquals("At store register. You can now scan the items or "
                + "register membership", moved);
    }

    @Test
    void toPosWithoutBasketIsRefused() {
        PosSession session = newSession();
        session.execute("enter store");

        String out = session.execute("to pos");

        assertTrue(out.contains("create basket"), out);
    }

    @Test
    void scanItemsPrintsRunningTotalsAndClosingLine() {
        PosSession session = enteredStoreWithBasket();
        session.execute("add soy-milk");
        session.execute("add orange-juice");
        session.execute("add orange-juice");
        session.execute("add apple 300");
        session.execute("to pos");

        String out = session.execute("scan items");

        assertTrue(out.contains("1 Alpro Soymilk scanned"), out);
        assertTrue(out.contains("Total: 34.96 SEK"), out);
        assertTrue(out.contains("2 Tropicana Orange Juice scanned"), out);
        assertTrue(out.contains("Total: 94.86 SEK"), out);
        assertTrue(out.contains("300g Apple scanned"), out);
        assertTrue(out.contains("Total: 102.32 SEK"), out);
        assertTrue(out.contains(
                "All items scanned. Go to \"payment\" or \"apply discount\""),
                out);

        assertEquals("All items have already been scanned.",
                session.execute("scan items"));
    }

    @Test
    void scanWithEmptyBasketIsRefused() {
        PosSession session = enteredStoreWithBasket();
        session.execute("to pos");

        assertEquals("Your basket is empty.", session.execute("scan items"));
    }

    @Test
    void registerCommandsAreRefusedInTheStore() {
        PosSession session = enteredStoreWithBasket();

        String out = session.execute("scan items");

        assertTrue(out.contains("to pos"), out);
    }

    @Test
    void validRegistrationMakesCustomerMemberWithGeneratedNumber() {
        PosSession session = atRegisterWithItems();

        String out = session.registerMembership("Ann", "19900101",
                "900101-0000");

        assertTrue(out.matches("(?s).*Congratulations! You can now use "
                + "discounts\\. Your member number is M\\d{6}!"), out);
        assertTrue(session.getCustomer().isMember());
    }

    @Test
    void malformedBirthdayAbortsRegistration() {
        PosSession session = atRegisterWithItems();

        String out = session.registerMembership("Ann", "notadate", "x");

        assertEquals("Invalid birthday format. Use yyyymmdd.", out);
        assertTrue(!session.getCustomer().isMember());
    }

    @Test
    void doubleRegistrationIsRefused() {
        PosSession session = atRegisterWithItems();
        session.registerMembership("Ann", "19900101", "x");

        String out = session.registerMembership("Ann", "19900101", "x");

        assertEquals("You are already a member.", out);
    }

    @Test
    void membershipRegistrationOnlyWorksAtTheRegister() {
        PosSession session = enteredStoreWithBasket();

        String refusal = session.membershipRegistrationRefusal();

        assertTrue(refusal.contains("to pos"), refusal);
    }

    private PosSession atRegisterWithItems() {
        PosSession session = enteredStoreWithBasket();
        session.execute("add soy-milk");
        session.execute("to pos");
        return session;
    }

    @Test
    void memberSeesDiscountSuccessMessage() {
        PosSession session = atRegisterWithItems();
        session.registerMembership("Ann", "19900101", "x");
        session.execute("scan items");

        String out = session.execute("apply discount");

        assertEquals("Discount has succeeded. Enjoy 20% off your purchase!",
                out);
        assertEquals("A discount has already been applied to this purchase.",
                session.execute("apply discount"));
    }

    @Test
    void nonMemberSeesDiscountFailureMessage() {
        PosSession session = atRegisterWithItems();
        session.execute("scan items");

        String out = session.execute("apply discount");

        assertEquals("Applying discount failed. You need to be a member",
                out);
    }

    @Test
    void discountBeforeScanningIsRefused() {
        PosSession session = atRegisterWithItems();

        String out = session.execute("apply discount");

        assertTrue(out.contains("scan items"), out);
    }

    @Test
    void createCardWithAmountIssuesRandomIssuerAndExactBalance() {
        PosSession session = enteredStoreWithBasket();

        String out = session.execute("create card 300");

        assertTrue(out.matches("A new (MasterCard|Visa|Amex) card has been "
                + "created with id \\d+\\. Balance: 300\\.00 SEK"), out);
        assertEquals(1, session.getCustomer().getCards().size());
        assertEquals(30000, session.getCustomer().getCards().get(0)
                .getBalance());
    }

    @Test
    void createCardDefaultsToFiveHundredKronor() {
        PosSession session = enteredStoreWithBasket();

        String out = session.execute("create card");

        assertTrue(out.endsWith("Balance: 500.00 SEK"), out);
    }

    @Test
    void createCardAcceptsDecimalAmounts() {
        PosSession session = enteredStoreWithBasket();

        String out = session.execute("create card 29.95");

        assertTrue(out.endsWith("Balance: 29.95 SEK"), out);
        assertEquals(2995,
                session.getCustomer().getCards().get(0).getBalance());
    }

    @Test
    void createCardRejectsGarbageAmountAndUnknownExtras() {
        PosSession session = enteredStoreWithBasket();

        String out = session.execute("create card lots");

        assertTrue(!out.contains("has been created"), out);
        assertTrue(session.getCustomer().getCards().isEmpty());
    }

    @Test
    void createCardBeforeEnteringStoreIsRefused() {
        PosSession session = newSession();

        String out = session.execute("create card 100");

        assertTrue(out.contains("enter store"), out);
    }
}

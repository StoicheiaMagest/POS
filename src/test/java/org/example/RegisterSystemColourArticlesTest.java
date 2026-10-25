package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.example.RegisterTestSupport.run;
import static org.junit.jupiter.api.Assertions.*;

// RegisterSystem2 with a registry the test builds itself, holding real items named after colours.
// The data is defined here, so literal names and prices are fine. None of the colour articles has
// a discount type, so the only discounts in these tests come from the customer.
public class RegisterSystemColourArticlesTest {

    private static final int RED = 1;     // 10,00 kr + 12% VAT = 11,2 SEK
    private static final int BLUE = 2;    // 25,00 kr + 25% VAT = 31,25 SEK
    private static final int GREEN = 3;   // weighed, 40,00 kr/kg + 6% VAT = 42,4 SEK/kg

    private ArticleRegistry colours() {
        ArticleRegistry registry = new ArticleRegistry();
        registry.addItem(RED, new UnitPricedArticle("Red", 1000, Vat.FOOD, RED));
        registry.addItem(BLUE, new UnitPricedArticle("Blue", 2500, Vat.STANDARD, BLUE));
        registry.addItem(GREEN, new WeighedItem("Green", 4000, Vat.BOOK, 0, GREEN));
        return registry;
    }

    private String run(String... commands) {
        return RegisterTestSupport.run(colours(), commands);
    }

    // The printed receipt without its date/time line, which differs between runs.
    private String receiptOf(String output) {
        int start = output.indexOf("Java Butiken");
        int end = output.indexOf("Totalt", start); // LIST prints a Totalt line before the receipt
        String receipt = output.substring(start, output.indexOf('\n', end));
        return receipt.replaceFirst("\n[^\n]*kvitto", "\nkvitto");
    }

    private int countOf(String text, String part) {
        int count = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) {
            count++;
        }
        return count;
    }

    // --- orders ---

    @Test
    void newOrderWithoutMemberOrSeniorIsAnonymous() {
        String output = run("NEW", "SCAN 1");

        assertTrue(output.contains("New order started for an anonymous customer"));
        assertFalse(output.contains("New order started for Member"));
        assertFalse(output.contains("New order started for Senior"));
    }

    @Test
    void scanningWithoutNewGivesError() {
        String output = run("SCAN 1");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Scanned 1"));
    }

    @Test
    void scanningAnArticleNumberNotInTheRegistryGivesErrorAndLeavesTheBasketAlone() {
        String output = run("NEW", "SCAN 1", "SCAN 99", "LIST");

        assertTrue(output.contains("Error, Article not found: 99"), output);
        assertFalse(output.contains("Scanned 99"));
        assertTrue(output.contains("Red  11,2 SEK\nTotalt  11,2 SEK"), output);
    }

    @Test
    void weighingAUnitPricedColourGivesError() {
        String output = run("NEW", "WEIGH 1 500");

        assertTrue(output.contains("Error, article 1 is not a weighed item!"), output);
    }

    // --- ARTICLES ---

    @Test
    void articlesListsTheArticlesInTheRegistry() {
        String output = run("ARTICLES");

        assertTrue(output.contains("1  Red  11,2 SEK\n2  Blue  31,25 SEK\n3  Green  42,4 SEK/kg"), output);
    }

    @Test
    void articlesWithAnEmptyRegistryListsNothingAndGivesNoError() {
        String output = RegisterTestSupport.run(new ArticleRegistry(), "ARTICLES");

        assertFalse(output.contains("Error"));
        assertFalse(output.contains(" SEK"));
    }

    @Test
    void articlesWorksWithoutAnOrderStarted() {
        String output = run("ARTICLES");

        assertFalse(output.contains("start an order"));
        assertTrue(output.contains("1  Red  11,2 SEK"));
    }

    @Test
    void articlesShowsTheSameArticlesDuringAnOrder() {
        String output = run("NEW", "SCAN 1", "WEIGH 3 500", "ARTICLES");

        assertTrue(output.contains("1  Red  11,2 SEK\n2  Blue  31,25 SEK\n3  Green  42,4 SEK/kg"), output);
        // a scanned article is neither removed from the list nor shown twice
        assertEquals(1, countOf(output, "1  Red  11,2 SEK"));
    }

    @Test
    void articlesDoesNotChangeTheBasket() {
        String output = run("NEW", "SCAN 1", "SCAN 2", "ARTICLES", "LIST");

        assertTrue(output.contains("Red  11,2 SEK\nBlue  31,25 SEK\nTotalt  42,45 SEK"), output);
        assertFalse(output.contains("Error"));
    }

    @Test
    void articlesIsListedInTheCommandHelpAtStartup() {
        String output = run("EXIT");

        assertTrue(output.contains("* ARTICLES"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"articles", "  ARTICLES  ", "ARTICLES 1"})
    void articlesIgnoresCaseSurroundingSpacesAndExtraArguments(String command) {
        String output = run(command);

        assertTrue(output.contains("1  Red  11,2 SEK\n2  Blue  31,25 SEK\n3  Green  42,4 SEK/kg"), output);
        assertFalse(output.contains("Error"));
    }

    // --- LIST ---

    @Test
    void listShowsScannedColoursWithPricesAndTotal() {
        String output = run("NEW", "SCAN 1", "SCAN 2", "LIST");

        assertTrue(output.contains("Red  11,2 SEK\nBlue  31,25 SEK\nTotalt  42,45 SEK"), output);
    }

    @Test
    void listShowsAWeighedColourWithItsWeight() {
        String output = run("NEW", "WEIGH 3 500", "LIST");

        assertTrue(output.contains("Green  500 g  21,2 SEK\nTotalt  21,2 SEK"), output);
    }

    @Test
    void listFormatsWeighedColoursOfDifferentWeights() {
        String output = run("NEW", "WEIGH 3 1", "WEIGH 3 250", "WEIGH 3 1000", "LIST");

        assertTrue(output.contains(
                "Green  1 g  0,04 SEK\nGreen  250 g  10,6 SEK\nGreen  1000 g  42,4 SEK\nTotalt  53,04 SEK"), output);
    }

    @Test
    void listShowsItemsInTheOrderTheyWereAdded() {
        String output = run("NEW", "WEIGH 3 500", "SCAN 2", "SCAN 1", "SCAN 2", "LIST");

        assertTrue(output.contains(
                "Green  500 g  21,2 SEK\nBlue  31,25 SEK\nRed  11,2 SEK\nBlue  31,25 SEK\nTotalt  94,9 SEK"), output);
    }

    @Test
    void scanningAnArticleWithoutADiscountTypeThreeTimesGivesNoItemDiscount() {
        String output = run("NEW", "SCAN 1", "SCAN 1", "SCAN 1", "LIST");

        assertTrue(output.contains("Red  11,2 SEK\nRed  11,2 SEK\nRed  11,2 SEK\nTotalt  33,6 SEK"), output);
        assertFalse(output.contains("3 for 2"));
    }

    @Test
    void listDoesNotShowRemovedItem() {
        String output = run("NEW", "SCAN 1", "SCAN 2", "REMOVE 1", "LIST");

        assertTrue(output.contains("Blue  31,25 SEK\nTotalt  31,25 SEK"), output);
        assertFalse(output.contains("Red  11,2 SEK"));
    }

    @Test
    void listOnEmptyBasketShowsNoItemsAndZeroTotal() {
        String output = run("NEW", "LIST");

        assertTrue(output.contains("Totalt  0 SEK"), output);
        assertFalse(output.contains("Red"));
        assertFalse(output.contains("Blue"));
        assertFalse(output.contains("Green"));
        assertFalse(output.contains("Error"));
    }

    @Test
    void listWithoutNewGivesError() {
        String output = run("LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        assertFalse(output.contains("Totalt"));
    }

    @Test
    void listAfterPaymentGivesErrorUntilNewOrderIsStarted() {
        String output = run("NEW", "SCAN 1", "PAY", "LIST");

        assertTrue(output.contains("Error, start an order with NEW first!"));
        // only the receipt from PAY has a Totalt line, LIST printed none
        assertEquals(1, countOf(output, "Totalt"));
    }

    @Test
    void listAfterNewOrderOnlyShowsTheNewOrdersItems() {
        String output = run("NEW", "SCAN 1", "NEW", "SCAN 2", "LIST");

        assertTrue(output.contains("Blue  31,25 SEK\nTotalt  31,25 SEK"), output);
        assertFalse(output.contains("Red  11,2 SEK"));
    }

    @Test
    void listDoesNotChangeTheBasket() {
        String withList = run("NEW", "SCAN 1", "SCAN 2", "LIST", "LIST", "PAY");
        String withoutList = run("NEW", "SCAN 1", "SCAN 2", "PAY");

        assertEquals(receiptOf(withoutList), receiptOf(withList));
    }

    @Test
    void listDoesNotPayOrPrintAReceipt() {
        String listOnly = run("NEW", "SCAN 1", "LIST");
        String listThenPay = run("NEW", "SCAN 1", "LIST", "PAY");

        assertFalse(listOnly.contains("Java Butiken"));
        assertFalse(listOnly.contains("kvitto"));
        // the receipt for the real payment is still number 1 and printed once
        assertEquals(1, countOf(listThenPay, "Java Butiken"));
        assertTrue(listThenPay.contains("kvitto 1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"list", "  LIST  ", "LIST 1"})
    void listIgnoresCaseSurroundingSpacesAndExtraArguments(String command) {
        String output = run("NEW", "SCAN 1", command);

        assertTrue(output.contains("Red  11,2 SEK\nTotalt  11,2 SEK"), output);
        assertFalse(output.contains("Error"));
    }

    // --- PAY ---

    @Test
    void payingPrintsTheColoursAndTotalOnTheReceipt() {
        String output = run("NEW", "SCAN 1", "WEIGH 3 500", "PAY");

        assertTrue(output.contains("Red  11,2 SEK\nGreen  500 g  21,2 SEK\nTotalt  32,4 SEK"), output);
    }

    // --- customers ---

    @Test
    void seniorGetsTenPercentOffTheColoursAndNothingElse() {
        // 11,2 + 31,25 = 42,45 kr, 10% rounded half-up is 4,25 kr
        String output = run("NEW SENIOR", "SCAN 1", "SCAN 2", "LIST");

        assertTrue(output.contains("Red  11,2 SEK\nBlue  31,25 SEK\nSenior 10%  -4,25 SEK\nTotalt  38,2 SEK"), output);
    }

    @Test
    void memberWithoutCouponsPaysFullPriceForColours() {
        String output = run("NEW MEMBER", "SCAN 1", "SCAN 2", "LIST");

        assertTrue(output.contains("Red  11,2 SEK\nBlue  31,25 SEK\nTotalt  42,45 SEK"), output);
    }
}

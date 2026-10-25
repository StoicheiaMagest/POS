package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

// The article file has one article per line: number;type;name;price;vat[;discount]
//   number   whole number greater than zero
//   type     UNIT or WEIGHED
//   price    whole öre excluding VAT, per kg for WEIGHED
//   vat      FOOD, STANDARD or BOOK
//   discount optional: 3FOR2 or 2KR_OFF (3FOR2 is not allowed on a WEIGHED article)
// Blank lines and lines starting with # are ignored. A bad line rejects the whole file with an
// IllegalArgumentException whose message starts with "Line <number>:".
public class ArticleRegistryLoaderTest {

    private ArticleRegistry load(String text) {
        return ArticleRegistryLoader.load(new Scanner(text));
    }

    // The message of the error the file gives. Fails the test if the file loads.
    private String errorFor(String text) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> load(text));
        return error.getMessage();
    }

    private void assertErrorOnLine(int line, String offending, String text) {
        String message = errorFor(text);
        assertTrue(message.startsWith("Line " + line + ":"), message);
        assertTrue(message.contains(offending), message);
    }

    // --- valid articles ---

    @Test
    void loadsAUnitPricedArticle() {
        ArticleRegistry registry = load("111;UNIT;Milk;400;FOOD");

        Item item = registry.findByArticleNumber(111);
        assertInstanceOf(UnitPricedArticle.class, item);
        assertEquals(111, item.getArticleNumber());
        assertEquals("Milk", item.getName());
        assertEquals(400, item.getPrice());
        assertEquals(Vat.FOOD, item.getVatRate());
        assertEquals(DiscountType.NONE, item.getDiscountType());
    }

    @Test
    void loadsAWeighedArticleAsATemplateWithNoWeight() {
        ArticleRegistry registry = load("333;WEIGHED;Apple;3000;BOOK");

        Item item = registry.findByArticleNumber(333);
        WeighedItem apple = assertInstanceOf(WeighedItem.class, item);
        assertEquals("Apple", apple.getName());
        assertEquals(3000, apple.getPricePerKg());
        assertEquals(0, apple.getWeight());
        assertEquals(Vat.BOOK, apple.getVatRate());
    }

    @Test
    void loadsSeveralArticles() {
        ArticleRegistry registry = load("""
                111;UNIT;Red;400;FOOD
                222;UNIT;Blue;150;STANDARD
                333;WEIGHED;Green;3000;BOOK
                """);

        assertEquals(List.of("Red", "Blue", "Green"),
                registry.getAllItems().stream().map(Item::getName).toList());
    }

    @Test
    void keepsSpacesInsideTheName() {
        ArticleRegistry registry = load("5;UNIT;Oat milk 1 l;1990;FOOD");

        assertEquals("Oat milk 1 l", registry.findByArticleNumber(5).getName());
    }

    @Test
    void trimsSpacesAroundTheFields() {
        ArticleRegistry registry = load("  111 ; UNIT ; Milk ; 400 ; FOOD  ");

        Item item = registry.findByArticleNumber(111);
        assertEquals("Milk", item.getName());
        assertEquals(400, item.getPrice());
        assertEquals(Vat.FOOD, item.getVatRate());
    }

    @Test
    void typeAndVatAreCaseInsensitive() {
        ArticleRegistry registry = load("""
                1;unit;Red;400;food
                2;Weighed;Green;3000;Standard
                """);

        assertInstanceOf(UnitPricedArticle.class, registry.findByArticleNumber(1));
        assertEquals(Vat.FOOD, registry.findByArticleNumber(1).getVatRate());
        assertInstanceOf(WeighedItem.class, registry.findByArticleNumber(2));
        assertEquals(Vat.STANDARD, registry.findByArticleNumber(2).getVatRate());
    }

    @Test
    void ignoresBlankLinesAndComments() {
        ArticleRegistry registry = load("""
                # number;type;name;price;vat

                111;UNIT;Red;400;FOOD
                   \s
                # a comment in the middle
                222;UNIT;Blue;150;FOOD
                """);

        assertEquals(2, registry.getAllItems().size());
    }

    @Test
    void readsWindowsLineEndings() {
        ArticleRegistry registry = load("111;UNIT;Red;400;FOOD\r\n222;UNIT;Blue;150;FOOD\r\n");

        assertEquals("Red", registry.findByArticleNumber(111).getName());
        assertEquals("Blue", registry.findByArticleNumber(222).getName());
    }

    @Test
    void anEmptyFileGivesAnEmptyRegistry() {
        assertTrue(load("").getAllItems().isEmpty());
    }

    @Test
    void aFileWithOnlyCommentsAndBlankLinesGivesAnEmptyRegistry() {
        assertTrue(load("# nothing here\n\n# really nothing\n").getAllItems().isEmpty());
    }

    // --- the optional discount column ---

    @Test
    void readsThreeForTwo() {
        ArticleRegistry registry = load("111;UNIT;Milk;400;FOOD;3FOR2");

        assertEquals(DiscountType.THREE_FOR_TWO, registry.findByArticleNumber(111).getDiscountType());
    }

    @Test
    void readsTwoKrOff() {
        ArticleRegistry registry = load("222;UNIT;Bread;150;FOOD;2KR_OFF");

        assertEquals(DiscountType.TWO_KR_OFF, registry.findByArticleNumber(222).getDiscountType());
    }

    @Test
    void theDiscountIsCaseInsensitive() {
        ArticleRegistry registry = load("111;UNIT;Milk;400;FOOD;3for2");

        assertEquals(DiscountType.THREE_FOR_TWO, registry.findByArticleNumber(111).getDiscountType());
    }

    @Test
    void aMissingOrEmptyDiscountMeansNoDiscount() {
        ArticleRegistry registry = load("""
                1;UNIT;Red;400;FOOD
                2;UNIT;Blue;150;FOOD;
                3;UNIT;Green;200;FOOD;  \s
                """);

        assertEquals(DiscountType.NONE, registry.findByArticleNumber(1).getDiscountType());
        assertEquals(DiscountType.NONE, registry.findByArticleNumber(2).getDiscountType());
        assertEquals(DiscountType.NONE, registry.findByArticleNumber(3).getDiscountType());
    }

    @Test
    void theDiscountDoesNotDependOnTheArticleNumber() {
        ArticleRegistry registry = load("""
                111;UNIT;Red;400;FOOD
                7;UNIT;Blue;400;FOOD;3FOR2
                """);

        assertEquals(DiscountType.NONE, registry.findByArticleNumber(111).getDiscountType());
        assertEquals(DiscountType.THREE_FOR_TWO, registry.findByArticleNumber(7).getDiscountType());
    }

    @Test
    void anUnknownDiscountIsAnError() {
        assertErrorOnLine(1, "HALF_PRICE", "111;UNIT;Milk;400;FOOD;HALF_PRICE");
    }

    @Test
    void threeForTwoIsNotAllowedOnAWeighedArticle() {
        assertEquals("Line 1: 3FOR2 cannot be used on a weighed article",
                errorFor("333;WEIGHED;Apple;3000;FOOD;3FOR2"));
    }

    @Test
    void twoKrOffIsAllowedOnAWeighedArticle() {
        ArticleRegistry registry = load("333;WEIGHED;Apple;3000;FOOD;2KR_OFF");

        assertEquals(DiscountType.TWO_KR_OFF, registry.findByArticleNumber(333).getDiscountType());
    }

    // --- bad lines reject the whole file ---

    @Test
    void tooFewFieldsIsAnError() {
        assertErrorOnLine(1, "111;UNIT;Milk;400", "111;UNIT;Milk;400");
    }

    @Test
    void tooManyFieldsIsAnError() {
        assertErrorOnLine(1, "extra", "111;UNIT;Milk;400;FOOD;3FOR2;extra");
    }

    @Test
    void aNonNumericArticleNumberIsAnError() {
        assertErrorOnLine(1, "abc", "abc;UNIT;Milk;400;FOOD");
    }

    @Test
    void anArticleNumberOfZeroIsAnError() {
        assertEquals("Line 1: article number must be positive", errorFor("0;UNIT;Milk;400;FOOD"));
    }

    @Test
    void aNegativeArticleNumberIsAnError() {
        assertEquals("Line 2: article number must be positive", errorFor("""
                1;UNIT;Red;400;FOOD
                -5;UNIT;Milk;400;FOOD
                """));
    }

    @Test
    void aNonNumericPriceIsAnError() {
        assertErrorOnLine(1, "4,00", "111;UNIT;Milk;4,00;FOOD");
    }

    @Test
    void aPriceOfZeroIsAnError() {
        assertErrorOnLine(1, "0", "111;UNIT;Milk;0;FOOD");
    }

    @Test
    void aNegativePriceIsAnError() {
        assertErrorOnLine(1, "-5", "111;UNIT;Milk;-5;FOOD");
    }

    @Test
    void anUnknownTypeIsAnError() {
        assertErrorOnLine(1, "PIECE", "111;PIECE;Milk;400;FOOD");
    }

    @Test
    void anUnknownVatRateIsAnError() {
        assertEquals("Line 3: unknown VAT rate 'FODD'", errorFor("""
                1;UNIT;Red;400;FOOD
                2;UNIT;Blue;150;FOOD
                3;UNIT;Green;200;FODD
                """));
    }

    @Test
    void anEmptyNameIsAnError() {
        assertErrorOnLine(1, "name", "111;UNIT;;400;FOOD");
    }

    @Test
    void aDuplicateArticleNumberIsAnErrorOnTheSecondLine() {
        assertEquals("Line 2: duplicate article number 111", errorFor("""
                111;UNIT;Red;400;FOOD
                111;UNIT;Blue;150;FOOD
                """));
    }

    @Test
    void lineNumbersCountCommentsAndBlankLines() {
        assertErrorOnLine(4, "abc", """
                # header

                111;UNIT;Red;400;FOOD
                abc;UNIT;Blue;150;FOOD
                """);
    }

    @Test
    void aBadLineAfterGoodOnesStillRejectsTheFile() {
        // no registry is returned, the good lines before the bad one are not loaded
        assertThrows(IllegalArgumentException.class, () -> load("""
                1;UNIT;Red;400;FOOD
                2;UNIT;Blue;150;FOOD
                3;UNIT;Green;oops;FOOD
                """));
    }
}

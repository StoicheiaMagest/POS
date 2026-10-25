package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitPricedArticleTest {

    @Test
    void constructor_should_set_article_properties() {
        UnitPricedArticle article = new UnitPricedArticle(
                "The Bible",
                10000,
                Vat.BOOK,
                1001
        );

        assertEquals("The Bible", article.getName(), "Name of the article was not set correctly");
        assertEquals(10000, article.getPrice(), "Price was not set correctly");
        assertEquals(Vat.BOOK, article.getVatRate(), "BOOK VAT rate is set incorrectly");
        assertEquals(1001, article.getArticleNumber(), "article id was not set correctly");
    }

    @Test
    void discount_type_defaults_to_none() {
        UnitPricedArticle article = new UnitPricedArticle("The Bible", 10000, Vat.BOOK, 1001);

        assertEquals(DiscountType.NONE, article.getDiscountType(), "An article has no discount unless one is given");
    }

    @Test
    void constructor_should_set_discount_type() {
        UnitPricedArticle threeForTwo = new UnitPricedArticle("Milk", 400, Vat.FOOD, 111, DiscountType.THREE_FOR_TWO);
        UnitPricedArticle twoKrOff = new UnitPricedArticle("Bread", 150, Vat.FOOD, 222, DiscountType.TWO_KR_OFF);

        assertEquals(DiscountType.THREE_FOR_TWO, threeForTwo.getDiscountType(), "3 for 2 was not set correctly");
        assertEquals(DiscountType.TWO_KR_OFF, twoKrOff.getDiscountType(), "2 kr off was not set correctly");
    }

    @Test
    void should_calculate_inclusive_price_correctly() {
        UnitPricedArticle article = new UnitPricedArticle(
                "The Bible",
                10000, // 10000 cents base price
                Vat.BOOK, // 6% VAT
                1001
        );

        // 10000 cents + 6% VAT (600 cents) = 10600 cents total
        assertEquals(10600, article.getInclusivePrice(), "Inclusive price with VAT was calculated incorrectly");
    }
}
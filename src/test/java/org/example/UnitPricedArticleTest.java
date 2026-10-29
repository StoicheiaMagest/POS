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

    @Test
    void should_calculate_vat_amount_correctly() {
        UnitPricedArticle article = new UnitPricedArticle(
                "The Bible",
                10000,
                Vat.BOOK,
                1001
        );

        assertEquals(600, article.getVatAmount(),
                "VAT amount was calculated incorrectly");
    }

}
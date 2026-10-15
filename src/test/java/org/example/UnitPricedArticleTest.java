
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitPricedArticleTest {

    @Test
    void constructor_should_set_article_properties() {
        UnitPricedArticle article = new UnitPricedArticle(
                "The Bible",
                200,
                VatRate.BOOK_6,
                1001,
                1
        );

        assertEquals("The Bible", article.getName(), "Name of the article was not set correctly");
        assertEquals(200, article.getPrice(), "Price was not set correctly");
        assertEquals(VatRate.BOOK_6, article.getVatRate(), "BOOK_6 VAT rate is set incorrectly");
        assertEquals(1001, article.getArticleId(), "article id was not set correctly");
    }
}

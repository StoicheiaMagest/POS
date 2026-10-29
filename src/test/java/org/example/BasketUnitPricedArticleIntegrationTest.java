
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasketUnitPricedArticleIntegrationTest {

    @Test
    void addUnitPricedArticleShouldIncludeVatInBasketTotal() {
        // Arrange
        Basket basket = new Basket();

        UnitPricedArticle book =
                new UnitPricedArticle("Book", 10000, Vat.BOOK, 1);

        // Act
        basket.addItem(book);

        // Assert
        assertEquals(10600L, book.getInclusivePrice());
        assertEquals(1, basket.getItems().size());
        assertTrue(basket.getItems().contains(book));
        assertEquals(10600, basket.getTotalPrice());
    }

    @Test
    void addMultipleArticlesShouldIncludeVatInBasketTotal() {
        // Arrange
        Basket basket = new Basket();

        UnitPricedArticle book =
                new UnitPricedArticle("Book", 10000, Vat.BOOK, 1);

        UnitPricedArticle notebook =
                new UnitPricedArticle("Notebook", 2000, Vat.STANDARD, 2);

        // Act
        basket.addItem(book);
        basket.addItem(notebook);

        // Assert
        assertEquals(10600L, book.getInclusivePrice());
        assertEquals(2500L, notebook.getInclusivePrice());
        assertEquals(13100, basket.getTotalPrice());
    }

    @Test
    void removeUnitPricedArticleShouldUpdateBasketTotalIncludingVat() {
        // Arrange
        Basket basket = new Basket();

        UnitPricedArticle book =
                new UnitPricedArticle("Book", 10000, Vat.BOOK, 1);

        UnitPricedArticle notebook =
                new UnitPricedArticle("Notebook", 2000, Vat.STANDARD, 2);

        basket.addItem(book);
        basket.addItem(notebook);

        // Act
        basket.removeItem(book);

        // Assert
        assertEquals(1, basket.getItems().size());
        assertFalse(basket.getItems().contains(book));
        assertTrue(basket.getItems().contains(notebook));
        assertEquals(2500, basket.getTotalPrice());
    }


    @Test
    void addRemoveAndAddUnitPricedArticleAgainShouldUpdateBasketCorrectly() {
        // Arrange
        Basket basket = new Basket();

        UnitPricedArticle tofu =
                new UnitPricedArticle("Tofu", 1600, Vat.STANDARD, 1);

        // Add Tofu.
        basket.addItem(tofu);

        assertEquals(2000L, basket.getTotalPrice());
        assertEquals(1, basket.getItems().size());
        assertTrue(basket.getItems().contains(tofu));

        // Remove Tofu.
        basket.removeItem(tofu);

        assertEquals(0L, basket.getTotalPrice());
        assertTrue(basket.getItems().isEmpty());

        // Add Tofu again.
        basket.addItem(tofu);

        assertEquals(2000L, basket.getTotalPrice());
        assertEquals(1, basket.getItems().size());
        assertTrue(basket.getItems().contains(tofu));

        // Add Tofu once more.
        basket.addItem(tofu);

        assertEquals(4000L, basket.getTotalPrice());
        assertEquals(2, basket.getItems().size());
    }

}

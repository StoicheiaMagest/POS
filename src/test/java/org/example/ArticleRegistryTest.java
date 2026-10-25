package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ArticleRegistryTest {
    @Test
    void findByArticleNumberReturnsRegisteredItem() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item item = mock(Item.class);

        registry.addItem(1001, item);

        // Act
        Item result = registry.findByArticleNumber(1001);

        // Assert
        assertSame(item, result);
    }


    @Test
    void findByArticleNumberReturnsNullWhenItemDoesNotExist() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();

        // Act
        Item result = registry.findByArticleNumber(9999);

        // Assert
        assertNull(result);
    }

    @Test
    void getAllItemsReturnsItemsSortedByArticleNumberWhateverTheOrderTheyWereAdded() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item red = new UnitPricedArticle("Red", 1000, Vat.FOOD, 1);
        Item blue = new UnitPricedArticle("Blue", 2500, Vat.STANDARD, 2);
        Item green = new WeighedItem("Green", 4000, Vat.BOOK, 0, 3);

        registry.addItem(3, green);
        registry.addItem(1, red);
        registry.addItem(2, blue);

        // Act
        List<Item> result = List.copyOf(registry.getAllItems());

        // Assert
        assertEquals(List.of(red, blue, green), result);
    }

    @Test
    void getAllItemsReturnsNothingForAnEmptyRegistry() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();

        // Act
        boolean empty = registry.getAllItems().isEmpty();

        // Assert
        assertTrue(empty);
    }

}

package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    void findAllReturnsItemsSortedByArticleNumber() {
        ArticleRegistry registry = new ArticleRegistry();
        Item first = mock(Item.class);
        Item second = mock(Item.class);
        registry.addItem(222, second);
        registry.addItem(111, first);

        assertEquals(List.of(first, second), registry.findAll());
    }

    @Test
    void findAllIsEmptyForEmptyRegistry() {
        assertEquals(List.of(), new ArticleRegistry().findAll());
    }

}

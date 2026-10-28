package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleRegistryTest {
    @Test
    void findByArticleNumberReturnsRegisteredItem() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item item = mock(Item.class);

        registry.registerItem(1001, item);

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
    void addItemThrowsExceptionWhenArticleNumberAlreadyExists() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        registry.registerItem(1001, firstItem);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerItem(1001, secondItem)
        );
    }

    @Test
    void addItemThrowsExceptionWhenItemIsNull() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerItem(1001, null)
        );
    }

    @Test
    void findByArticleNumberReturnsCorrectItemWhenMultipleItemsAreRegistered() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        registry.registerItem(1001, firstItem);
        registry.registerItem(1002, secondItem);

        // Act
        Item firstResult = registry.findByArticleNumber(1001);
        Item secondResult = registry.findByArticleNumber(1002);

        // Assert
        assertSame(firstItem, firstResult);
        assertSame(secondItem, secondResult);
    }

    @Test
    void addItemDoesNotReplaceExistingItemWhenArticleNumberAlreadyExists() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        registry.registerItem(1001, firstItem);

        // Act
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerItem(1001, secondItem)
        );

        // Assert
        assertSame(firstItem, registry.findByArticleNumber(1001));
    }

    @Test
    void addItemThrowsExceptionWhenArticleNumberIsNegative() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item item = mock(Item.class);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerItem(-1, item)
        );
    }



}

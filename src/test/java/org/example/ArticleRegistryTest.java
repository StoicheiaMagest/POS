package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class ArticleRegistryTest {
    @Test
    void findByArticleNumberReturnsRegisteredItem() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item item = mock(Item.class);

        registry.addItem("1001", item);

        // Act
        Item result = registry.findByArticleNumber("1001");

        // Assert
        assertSame(item, result);
    }
}

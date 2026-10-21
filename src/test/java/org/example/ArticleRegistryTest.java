package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArticleRegistryTest {
    @Test
    void findByArticleNumberReturnsRegisteredItem() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item item = mock(Item.class);
        when(item.getName()).thenReturn("Mocked Item");

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
    void findByNameIsCaseInsensitive() {
        // Arrange
        ArticleRegistry registry = new ArticleRegistry();
        Item milk = new UnitPricedArticle("soy-milk", 3121, Vat.FOOD, 1,
                "Alpro Soymilk");
        registry.addItem(1, milk);

        // Act & Assert
        assertSame(milk, registry.findByName("SOY-MILK"));
        assertSame(milk, registry.findByName("soy-milk"));
    }

    @Test
    void standardAssortmentContainsTwelveArticlesWithSpecPrices() {
        // Arrange: spec §5 handles and VAT-inclusive display prices (cents)
        String[][] expected = {
            {"soy-milk", "3496"}, {"oat-drink", "3295"},
            {"orange-juice", "2995"}, {"hummus", "2495"},
            {"tofu", "2796"}, {"black-beans", "1995"},
            {"dark-chocolate", "2295"}, {"candle", "4995"},
            {"apple", "2490"}, {"banana", "1590"},
            {"vegan-cheese", "19900"}, {"olives", "14950"}
        };
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();

        // Act & Assert: every handle resolves and displays the §5 price
        for (String[] row : expected) {
            Item item = registry.findByName(row[0]);
            assertNotNull(item, "missing article: " + row[0]);
            assertEquals(Long.parseLong(row[1]), item.getInclusivePrice(),
                    "wrong inclusive price for " + row[0]);
        }
    }

}

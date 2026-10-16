package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class WeighedItemTest {

    @Test
    void constructor_should_set_article_properties() {
        WeightedItem weightedItem = new WeightedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg
                Vat.FOOD,
                500, // 500 grams
                42   // article id
        );

        assertEquals("Pink Lady Apple", weightedItem.getName(), "Name of the article was not set correctly");
        assertEquals(50, weightedItem.getPrice(), "Price for the given weight was not calculated correctly");
        assertEquals(Vat.FOOD, weightedItem.getVatRate(), "FOOD VAT rate was not set correctly");
        assertEquals(42, weightedItem.getArticleId(), "Article id was not set correctly");
        assertEquals(500, weightedItem.getWeight(), "Weight was not set correctly");
    }

    @Test
    void should_calculate_inclusive_price_correctly() {
        WeightedItem weightedItem = new WeightedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg base price
                Vat.FOOD,
                500, // 500 grams -> base price = 50 cents
                42
        );

        // Validates that inclusive price calculation works correctly using integer math
        long expectedInclusivePrice = Vat.FOOD.calculateInclusivePrice(50);
        assertEquals(expectedInclusivePrice, weightedItem.getInclusivePrice(), "Inclusive price with VAT was calculated incorrectly");
    }
}
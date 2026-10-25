package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class WeighedItemTest {

    @Test
    void constructor_should_set_article_properties() {
        WeighedItem weighedItem = new WeighedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg
                Vat.FOOD,
                500, // 500 grams
                42   // article id
        );

        assertEquals("Pink Lady Apple", weighedItem.getName(), "Name of the article was not set correctly");
        assertEquals(50, weighedItem.getPrice(), "Price for the given weight was not calculated correctly");
        assertEquals(Vat.FOOD, weighedItem.getVatRate(), "FOOD VAT rate was not set correctly");
        assertEquals(42, weighedItem.getArticleNumber(), "Article id was not set correctly");
        assertEquals(500, weighedItem.getWeight(), "Weight was not set correctly");
    }

    @Test
    void discount_type_defaults_to_none() {
        WeighedItem weighedItem = new WeighedItem("Pink Lady Apple", 100, Vat.FOOD, 500, 42);

        assertEquals(DiscountType.NONE, weighedItem.getDiscountType(), "An article has no discount unless one is given");
    }

    @Test
    void constructor_should_set_two_kr_off() {
        WeighedItem weighedItem = new WeighedItem("Pink Lady Apple", 100, Vat.FOOD, 500, 42, DiscountType.TWO_KR_OFF);

        assertEquals(DiscountType.TWO_KR_OFF, weighedItem.getDiscountType(), "2 kr off was not set correctly");
    }

    @Test
    void three_for_two_is_not_allowed_on_a_weighed_item() {
        assertThrows(IllegalArgumentException.class,
                () -> new WeighedItem("Pink Lady Apple", 100, Vat.FOOD, 500, 42, DiscountType.THREE_FOR_TWO));
    }

    @Test
    void withWeight_keeps_the_discount_type() {
        WeighedItem template = new WeighedItem("Pink Lady Apple", 100, Vat.FOOD, 0, 42, DiscountType.TWO_KR_OFF);

        WeighedItem weighed = template.withWeight(750);

        assertEquals(DiscountType.TWO_KR_OFF, weighed.getDiscountType(), "The weighed copy lost the discount type");
        assertEquals(750, weighed.getWeight(), "Weight was not set correctly");
    }

    @Test
    void should_calculate_inclusive_price_correctly() {
        WeighedItem weighedItem = new WeighedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg base price
                Vat.FOOD,
                500, // 500 grams -> base price = 50 cents
                42
        );

        // Validates that inclusive price calculation works correctly using integer math
        long expectedInclusivePrice = Vat.FOOD.calculateInclusivePrice(50);
        assertEquals(expectedInclusivePrice, weighedItem.getInclusivePrice(), "Inclusive price with VAT was calculated incorrectly");
    }
}
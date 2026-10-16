package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class WeighedItemTest {

    @Test
    void constructor_should_set_article_properties() {
        WeightedItem weighedItem = new WeightedItem(
                "Pink Lady Apple",
                100,
                VatRate.FOOD_12,
                5,
                42,
                20

        );

        assertEquals("Pink Lady Apple", weighedItem.getName(), "Name of the article was not set correctly");
        //assertEquals(20.00, weighedItem.getPrice(super(price)), 0.000001, "Price was not set correctly");
        assertEquals(20.00, weighedItem.getPricePerKg(), 0.000001, "Price per Kg was not set correctly");
        assertEquals(VatRate.FOOD_12, weighedItem.getVatRate(), "FOOD_12 VAT rate was not set correctly");
        assertEquals(42, weighedItem.getArticleId(), "article id was not set correctly");
    }



}

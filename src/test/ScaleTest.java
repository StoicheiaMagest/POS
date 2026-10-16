package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScaleTest {

    private Scale scale = new Scale();
    private WeightedItem wI = new WeightedItem("Pink Lady Apple", 100, VatRate.FOOD_12, 5, 42, 20);
    
    @Test
    void scale_should_return_kgPrice() {
        assertEquals(100, scale.setPrice(wI.getWeight(), wI.getPricePerKg()));
    }

}

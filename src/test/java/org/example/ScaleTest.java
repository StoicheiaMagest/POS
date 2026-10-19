package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ScaleTest {

    private Scale scale = new Scale();
    WeighedItem wi = new WeighedItem(
                "Pink Lady Apple",
                100, // 100 cents per kg
                Vat.FOOD,
                500, // 500 grams
                42   // article id
        );
    
    @Test
    void scale_should_return_correct_price() {
        assertEquals(50, scale.weightPrice(wi));
    }

}

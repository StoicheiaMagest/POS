package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketTest {
    private final Basket basket = new Basket();

    @Test
    void empty_basket_has_zero_price() {
        assertEquals(0, basket.getTotalPrice(), "Total price for the empty basket should be zero");
    }

    // Test som alltid fallerar
//    @Test
//    void testGetTotalPriceFailed() {
//        assertEquals(0, 1, "It returns 1 and expected 0");
//    }

}





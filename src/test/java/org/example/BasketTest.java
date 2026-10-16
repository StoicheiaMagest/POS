package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BasketTest {
    private final Basket basket = new Basket();

    @Test
    void empty_basket_has_zero_price() {
        assertEquals(0, basket.getTotalPrice(), "Total price for the empty basket should be zero");
    }

//    /**
//    @Test
//    void basket_with_one_item_has_that_items_price() {
//        // Arrange
//        Basket basket = new Basket();
//        //Item sojmilk = sojmilk /* skapa en artikel som kostar 1000 öre */;
//
//        // Act
//        basket.addItem(sojmilk);
//
//        // Assert
//        assertEquals(1000, basket.getTotalPrice());
//    }

}





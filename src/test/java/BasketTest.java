import org.example.Basket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketTest {
    private final Basket basket = new Basket();

    // Test som passerar så länge getTotalPrice är 0
    @Test
    void testGetTotalPricePassed() {
        assertEquals(0, basket.getTotalPrice(), "It returns 0 and expected 0");
    }


    // Test som alltid fallerar
    @Test
    void testGetTotalPriceFailed() {
        assertEquals(0, 1, "It returns 1 and expected 0");
    }

}



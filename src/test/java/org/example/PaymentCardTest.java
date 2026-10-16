package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


class PaymentCardTest {



    @Test
    void returnsCardId() {
        PaymentCard card = new PaymentCard(1023);
        assertEquals(1023, card.getCardId() );

    }
}

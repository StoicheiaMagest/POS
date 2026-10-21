package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardTest {

    private Account sender;
    private Account recipient;
    private Card card;

    @BeforeEach
    void setUp() {
        sender = mock(Account.class);
        recipient = mock(Account.class);
        card = new Card(sender);
    }

    @Test
    void pay_passesAmountAndRecipientToSenderAccount() {
        when(sender.transfer(40, recipient)).thenReturn(true);

        boolean result = card.pay(40, recipient);

        assertTrue(result);
        verify(sender).transfer(40, recipient);
    }

    @Test
    void pay_returnsFalseWhenSenderTransferFails() {
        when(sender.transfer(40, recipient)).thenReturn(false);

        boolean result = card.pay(40, recipient);

        assertFalse(result);
        verify(sender).transfer(40, recipient);
    }
}

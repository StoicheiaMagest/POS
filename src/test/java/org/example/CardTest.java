package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardTest {

    @Mock
    private Account sender;

    @Mock
    private Account recipient;

    @InjectMocks
    private Card card;

    @Test
    void pay_whenTransferSucceeds_returnsTrueAndTransfersToRecipient() {
        when(sender.transfer(40, recipient)).thenReturn(true);

        assertTrue(card.pay(40, recipient));

        verify(sender).transfer(40, recipient);
        verifyNoMoreInteractions(sender);
    }

    @Test
    void pay_whenTransferFails_returnsFalse() {
        when(sender.transfer(40, recipient)).thenReturn(false);

        assertFalse(card.pay(40, recipient));

        verify(sender).transfer(40, recipient);
        verifyNoMoreInteractions(sender);
    }
}

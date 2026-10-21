package org.example;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the customer model of spec &sect;6: identity, membership (joined
 * at the register), payment cards and location status.
 */
class CustomerTest {

    @Test
    void newCustomerIsNonMemberShoppingWithNoCards() {
        Customer customer = new Customer("Alex Smith", "19900101-1234");

        assertEquals("Alex Smith", customer.getName());
        assertEquals("19900101-1234", customer.getPersonId());
        assertFalse(customer.isMember());
        assertNull(customer.getBirthday());
        assertNull(customer.getMemberNumber());
        assertEquals(CustomerStatus.IN_STORE, customer.getStatus());
        assertTrue(customer.getCards().isEmpty());
    }

    @Test
    void joinMembershipSetsMemberFlagBirthdayAndNumber() {
        Customer customer = new Customer("Alex Smith", "19900101-1234");

        customer.joinMembership(LocalDate.of(1990, 1, 1), "M123456");

        assertTrue(customer.isMember());
        assertEquals(LocalDate.of(1990, 1, 1), customer.getBirthday());
        assertEquals("M123456", customer.getMemberNumber());
    }

    @Test
    void goToRegisterChangesStatus() {
        Customer customer = new Customer("Alex Smith", "19900101-1234");

        customer.goToRegister();

        assertEquals(CustomerStatus.AT_REGISTER, customer.getStatus());
    }

    @Test
    void cardsAreAddedAndListedAsACopy() {
        Customer customer = new Customer("Alex Smith", "19900101-1234");
        PaymentCard card = new PaymentCard(1, "Visa", new Account(100));

        customer.addCard(card);

        List<PaymentCard> cards = customer.getCards();
        assertEquals(1, cards.size());
        assertSame(card, cards.get(0));

        cards.clear();
        assertEquals(1, customer.getCards().size(),
                "the returned list must be a defensive copy");
    }
}

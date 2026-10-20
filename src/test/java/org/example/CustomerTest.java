package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void gettersReturnConstructorValues() {
        Customer customer = new Customer("Alex Smith", "19900101-1234", true, false);

        assertEquals("Alex Smith", customer.getName());
        assertEquals("19900101-1234", customer.getPersonId());
        assertTrue(customer.getMembershipStatus());
        assertFalse(customer.getSeniorStatus());
    }

    @Test
    void booleanGettersReturnFalseWhenConstructedWithFalse() {
        Customer customer = new Customer("Sam Lee", "19850515-5678", false, false);

        assertFalse(customer.getMembershipStatus());
        assertFalse(customer.getSeniorStatus());
    }

    @Test
    void booleanGettersReturnTrueWhenConstructedWithTrue() {
        Customer customer = new Customer("Taylor Kim", "19500101-9012", true, true);

        assertTrue(customer.getMembershipStatus());
        assertTrue(customer.getSeniorStatus());
    }

    @Test
    void gettersReturnEmptyStringsWhenConstructedWithEmptyStrings() {
        Customer customer = new Customer("", "", false, false);

        assertEquals("", customer.getName());
        assertEquals("", customer.getPersonId());
    }
}

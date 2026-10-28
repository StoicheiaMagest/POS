package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;

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

    @Test 
    void addNewCoupon(){
        Coupon coupon = new Coupon("20krOff", 20);
        LinkedList<Coupon> coupons = new LinkedList<>();
        coupons.add(coupon);
        
        
        Customer customer = new Customer("", "", false, false);
        customer.addCoupon(coupon);

        assertEquals(coupons, customer.getCoupons());
    }
    @Test 
    void addMultipleCoupons(){
        Coupon coupon1 = new Coupon("20krOff", 20);
        Coupon coupon2 = new Coupon("30krOff", 30);
        LinkedList<Coupon> coupons = new LinkedList<>();
        coupons.add(coupon1);
        coupons.add(coupon2);
        
        
        Customer customer = new Customer("", "", false, false);
        customer.addCoupon(coupon1);
        customer.addCoupon(coupon2);

        assertEquals(coupons, customer.getCoupons());
    }
}

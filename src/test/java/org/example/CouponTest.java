package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class CouponTest {

    @Test
    public void couponWithExpiryDateInThePastIsExpired(){
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Coupon coupon = new Coupon("20krOff", 20, yesterday);
        assertTrue(coupon.isExpired());
    }

    @Test
    public void couponWithExpiryDateInTheFutureIsNotExpired(){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Coupon coupon = new Coupon("20krOff", 20, tomorrow);
        assertFalse(coupon.isExpired());
    }

    @Test 
    public void createCouponWithoutNameIsInvalid(){

    }

    @Test 
    public void createCouponWithNegativeReducitonIsInvalid(){
        //Coupon coupon = new Coupon("20krOff", -20);
    }

    //test for use coupon

    @Test 
    public void createValidCoupon(){
        Coupon coupon = new Coupon("20krOff", 20, LocalDateTime.now().plusDays(1));
        assertEquals("20krOff", coupon.getName());
        assertEquals(20, coupon.getReduction());
    }
}

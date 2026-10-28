package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CouponTest {

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
        Coupon coupon = new Coupon("20krOff", 20);
        assertEquals("20krOff", coupon.getName());
        assertEquals(20, coupon.getReduction());
    }
}

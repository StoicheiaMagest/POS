package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;


public class DiscountTest {
    private final Discount discount = new Discount();
    private final Basket basket = new Basket();


    @Test 
    void Discount_for_200kr_purchace_is_20kr(){

        //when(basket.getTotalPrice()>=200);

        //discount.add20krDiscount();
        //assertEquals(20,discount.totalDiscountSum());
    }

}

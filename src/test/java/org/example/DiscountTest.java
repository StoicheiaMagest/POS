package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

public class DiscountTest {
    private final Discount discount = new Discount();

    private Item item = mock(Item.class);

    //getDiscountType tests
    @Test
    void Discount_type_is_3for2(){
        when(item.getArticleNumber()).thenReturn(111);
        assertEquals("3for2", discount.getDiscountType(item));
    }

    @Test
    void Discount_type_is_2p(){
        when(item.getArticleNumber()).thenReturn(222);
        assertEquals("2krOff", discount.getDiscountType(item));
    }

    @Test 
    void Discount_nonexsisting(){
        when(item.getArticleNumber()).thenReturn(101);
        assertEquals(null,discount.getDiscountType(item));
    }

    //getAllDiscounts tests
    @Test
    void Discount_total_is_returned(){
        Item bread = mock(Item.class);
        when(bread.getArticleNumber()).thenReturn(222);
        when(bread.getName()).thenReturn("Bread");
        when(bread.getInclusivePrice()).thenReturn(1700L);

        Basket basket = new Basket();
        basket.addItem(bread);
        basket.addItem(bread);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on " + bread.getName(), 400L);

        assertEquals(expected, discount.getAllDiscounts(basket, null));
    }

    @Test
    void non_member_only_gets_item_discount(){
        Item milk = mock(Item.class);
        when(milk.getArticleNumber()).thenReturn(111);
        when(milk.getName()).thenReturn("Milk");
        when(milk.getInclusivePrice()).thenReturn(500L);

        Basket basket = new Basket();
        for (int i = 0; i < 3; i++) {
            basket.addItem(milk);
        }

        Customer nonMember = new Customer("Anna", "19900101-1234", false, true);
        nonMember.addCoupon(Coupon.percentCoupon("10 %", 10, 0));

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 500L);

        // senior status and coupon are ignored without membership
        assertEquals(expected, discount.getAllDiscounts(basket, nonMember));
    }

    @Test
    void senior_discount_stacks_on_item_discount(){
        Item milk = mock(Item.class);
        when(milk.getArticleNumber()).thenReturn(111);
        when(milk.getName()).thenReturn("Milk");
        when(milk.getInclusivePrice()).thenReturn(500L);

        Basket basket = new Basket();
        for (int i = 0; i < 3; i++) {
            basket.addItem(milk);
        }

        Customer senior = new Customer("Stig", "19500101-1234", true, true);

        Map<String, Long> discounts = discount.getAllDiscounts(basket, senior);
        assertEquals(500L, discounts.get("3 for 2 Milk"));
        assertEquals(100L, discounts.get("Senior 10%")); // 10 % of the remaining 1000
    }

    @Test
    void percent_coupon_is_applied_for_members(){
        Item soap = mock(Item.class);
        when(soap.getArticleNumber()).thenReturn(333);
        when(soap.getInclusivePrice()).thenReturn(1000L);

        Basket basket = new Basket();
        basket.addItem(soap);

        Customer member = new Customer("Anna", "19900101-1234", true, false);
        Coupon coupon = Coupon.percentCoupon("10 %", 10, 0);

        Map<String, Long> discounts = discount.getAllDiscounts(basket, member, coupon);
        assertEquals(100L, discounts.get("Coupon 10 %"));
    }
}

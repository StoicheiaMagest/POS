package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

public class DiscountTest {
    private final Discount discount = new Discount();

    private UnitPricedArticle unit(String name, int priceExcl, int articleNumber, DiscountType type) {
        return new UnitPricedArticle(name, priceExcl, Vat.STANDARD, articleNumber, type); // 400 -> 500 incl
    }

    private Basket basketOf(int times, Item item) {
        Basket basket = new Basket();
        for (int i = 0; i < times; i++) {
            basket.addItem(item);
        }
        return basket;
    }

    //the item discount comes from the item's discount type
    @Test
    void three_for_two_comes_from_the_items_discount_type_not_the_article_number(){
        Item milk = unit("Milk", 400, 7, DiscountType.THREE_FOR_TWO);

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 500L);

        assertEquals(expected, discount.getAllDiscounts(basketOf(3, milk), null));
    }

    @Test
    void two_kr_off_comes_from_the_items_discount_type_not_the_article_number(){
        Item bread = unit("Bread", 150, 8, DiscountType.TWO_KR_OFF);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Bread", 200L);

        assertEquals(expected, discount.getAllDiscounts(basketOf(1, bread), null));
    }

    @Test
    void articles_111_and_222_get_no_discount_without_a_discount_type(){
        Item milk = unit("Milk", 400, 111, DiscountType.NONE);
        Item bread = unit("Bread", 150, 222, DiscountType.NONE);

        Basket basket = basketOf(3, milk);
        basket.addItem(bread);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basket, null));
    }

    @Test
    void three_for_two_needs_three_of_the_same_article(){
        Item milk = unit("Milk", 400, 111, DiscountType.THREE_FOR_TWO);
        Item juice = unit("Juice", 400, 112, DiscountType.THREE_FOR_TWO);

        Basket basket = basketOf(2, milk);
        basket.addItem(juice);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basket, null));
    }

    @Test
    void three_for_two_gives_one_free_per_full_three(){
        Item milk = unit("Milk", 400, 111, DiscountType.THREE_FOR_TWO);

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 1000L); // 7 milks: two groups of three, the seventh pays full

        assertEquals(expected, discount.getAllDiscounts(basketOf(7, milk), null));
    }

    @Test
    void two_kr_off_is_given_per_item_in_the_basket(){
        Item bread = unit("Bread", 150, 222, DiscountType.TWO_KR_OFF);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Bread", 600L);

        assertEquals(expected, discount.getAllDiscounts(basketOf(3, bread), null));
    }

    @Test
    void two_kr_off_on_a_weighed_item_is_given_per_weighing(){
        WeighedItem apple = new WeighedItem("Apple", 3000, Vat.FOOD, 0, 333, DiscountType.TWO_KR_OFF);
        Basket basket = new Basket();
        basket.addItem(apple.withWeight(500));
        basket.addItem(apple.withWeight(250));

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Apple", 400L); // two weighings, not per kg

        assertEquals(expected, discount.getAllDiscounts(basket, null));
    }

    @Test
    void items_without_a_discount_type_get_no_item_discount(){
        Item soap = unit("Soap", 400, 333, DiscountType.NONE);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basketOf(5, soap), null));
    }

    //getAllDiscounts tests
    @Test
    void Discount_total_is_returned(){
        Item bread = mock(Item.class);
        when(bread.getArticleNumber()).thenReturn(222);
        when(bread.getDiscountType()).thenReturn(DiscountType.TWO_KR_OFF);
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
        when(milk.getDiscountType()).thenReturn(DiscountType.THREE_FOR_TWO);
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
        when(milk.getDiscountType()).thenReturn(DiscountType.THREE_FOR_TWO);
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
        when(soap.getDiscountType()).thenReturn(DiscountType.NONE);
        when(soap.getInclusivePrice()).thenReturn(1000L);

        Basket basket = new Basket();
        basket.addItem(soap);

        Customer member = new Customer("Anna", "19900101-1234", true, false);
        Coupon coupon = Coupon.percentCoupon("10 %", 10, 0);

        Map<String, Long> discounts = discount.getAllDiscounts(basket, member, coupon);
        assertEquals(100L, discounts.get("Coupon 10 %"));
    }
}

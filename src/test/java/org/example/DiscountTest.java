package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        assertEquals(expected, discount.getAllDiscounts(basketOf(1, bread), customer(true, false)));
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

        assertEquals(expected, discount.getAllDiscounts(basketOf(3, bread), customer(true, false)));
    }

    @Test
    void two_kr_off_on_a_weighed_item_is_given_per_weighing(){
        WeighedItem apple = new WeighedItem("Apple", 3000, Vat.FOOD, 0, 333, DiscountType.TWO_KR_OFF);
        Basket basket = new Basket();
        basket.addItem(apple.withWeight(500));
        basket.addItem(apple.withWeight(250));

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Apple", 400L); // two weighings, not per kg

        assertEquals(expected, discount.getAllDiscounts(basket, customer(true, false)));
    }

    @Test
    void items_without_a_discount_type_get_no_item_discount(){
        Item soap = unit("Soap", 400, 333, DiscountType.NONE);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basketOf(5, soap), null));
    }

    //2 kr off is for members only, 3 for 2 is for everyone
    private Customer customer(boolean member, boolean senior) {
        return new Customer("Test", "19900101-1234", member, senior);
    }

    @Test
    void anonymous_customer_gets_no_two_kr_off(){
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basketOf(2, bread), null));
    }

    @Test
    void member_gets_two_kr_off(){
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Bread", 400L);

        assertEquals(expected, discount.getAllDiscounts(basketOf(2, bread), customer(true, false)));
    }

    @Test
    void non_member_senior_gets_no_two_kr_off_and_no_senior_discount(){
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(basketOf(2, bread), customer(false, true)));
    }

    @Test
    void non_member_senior_still_gets_three_for_two(){
        Item milk = unit("Milk", 400, 111, DiscountType.THREE_FOR_TWO);
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);
        Basket basket = basketOf(3, milk);
        basket.addItem(bread);
        basket.addItem(bread);

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 500L);

        assertEquals(expected, discount.getAllDiscounts(basket, customer(false, true)));
    }

    @Test
    void member_gets_both_three_for_two_and_two_kr_off(){
        Item milk = unit("Milk", 400, 111, DiscountType.THREE_FOR_TWO);
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);
        Basket basket = basketOf(3, milk);
        basket.addItem(bread);
        basket.addItem(bread);

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 500L);
        expected.put("2 kr off on Bread", 400L);

        assertEquals(expected, discount.getAllDiscounts(basket, customer(true, false)));
    }

    //2 kr off is never more than the item costs
    @Test
    void two_kr_off_is_capped_at_the_price_of_an_item_that_costs_less(){
        Item pencil = unit("Pencil", 120, 223, DiscountType.TWO_KR_OFF);    // 150 incl

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Pencil", 300L); // 2 x 150, not 2 x 200

        assertEquals(expected, discount.getAllDiscounts(basketOf(2, pencil), customer(true, false)));
    }

    @Test
    void two_kr_off_on_an_item_that_costs_exactly_two_kr_takes_all_of_it(){
        Item gum = unit("Gum", 160, 224, DiscountType.TWO_KR_OFF);          // 200 incl

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Gum", 400L);

        assertEquals(expected, discount.getAllDiscounts(basketOf(2, gum), customer(true, false)));
    }

    @Test
    void two_kr_off_is_capped_for_each_article_on_its_own(){
        Item pencil = unit("Pencil", 120, 223, DiscountType.TWO_KR_OFF);    // 150 incl
        Item bread = unit("Bread", 800, 222, DiscountType.TWO_KR_OFF);      // 1000 incl
        Basket basket = basketOf(1, pencil);
        basket.addItem(bread);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Pencil", 150L);
        expected.put("2 kr off on Bread", 200L);

        assertEquals(expected, discount.getAllDiscounts(basket, customer(true, false)));
    }

    @Test
    void two_kr_off_on_a_tiny_weighing_is_capped_at_its_price(){
        WeighedItem apple = new WeighedItem("Apple", 3000, Vat.FOOD, 0, 333, DiscountType.TWO_KR_OFF);
        WeighedItem oneGram = apple.withWeight(1);
        Basket basket = basketOf(1, oneGram);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Apple", oneGram.getInclusivePrice());

        assertEquals(expected, discount.getAllDiscounts(basket, customer(true, false)));
        assertTrue(oneGram.getInclusivePrice() < 200); // the cap is what limits it
    }

    @Test
    void a_senior_member_gets_every_discount_in_priority_order(){
        Item milk = unit("Milk", 400, 111, DiscountType.THREE_FOR_TWO);          // 500 incl
        Item bread = unit("Bread", 1000, 222, DiscountType.TWO_KR_OFF);          // 1250 incl
        Basket basket = basketOf(3, milk);
        basket.addItem(bread);
        basket.addItem(bread);                                                    // 4000 in total
        Customer seniorMember = customer(true, true);
        seniorMember.addCoupon(Coupon.fixedCoupon("20 kr", 200, 2000));

        Map<String, Long> discounts = discount.getAllDiscounts(basket, seniorMember);

        // 3 for 2: 500, 2 kr off: 400, coupon on the remaining 3100: 200, senior 10 % of the remaining 2900: 290
        assertEquals(List.of("3 for 2 Milk", "2 kr off on Bread", "Coupon 20 kr", "Senior 10%"),
                new ArrayList<>(discounts.keySet()));
        assertEquals(List.of(500L, 400L, 200L, 290L), new ArrayList<>(discounts.values()));
    }

    @Test
    void senior_discount_is_rounded_half_up(){
        Item item = mock(Item.class);
        when(item.getArticleNumber()).thenReturn(333);
        when(item.getDiscountType()).thenReturn(DiscountType.NONE);
        when(item.getInclusivePrice()).thenReturn(1005L);

        Map<String, Long> discounts = discount.getAllDiscounts(basketOf(1, item), customer(true, true));

        assertEquals(101L, discounts.get("Senior 10%")); // 10 % of 1005 is 100,5
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

        Customer member = new Customer("Anna", "19900101-1234", true, false);
        assertEquals(expected, discount.getAllDiscounts(basket, member));
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

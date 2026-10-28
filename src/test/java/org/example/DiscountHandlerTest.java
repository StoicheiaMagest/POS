package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DiscountHandlerTest {
    private final DiscountHandler discount = new DiscountHandler();
    private final Item milk = mock(Item.class);
    private final Item bread = mock(Item.class);
    private final Item oboy = mock(Item.class);
    private final int seniorDiscountPercentage = 10;

    private final Map<String, Long> expectedMap = new HashMap<>();
    private final Map<Integer, Long> expectedCounterMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        expectedMap.clear();
        expectedCounterMap.clear();

        when(milk.getArticleNumber()).thenReturn(1001);
        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000L);
        when(milk.getInclusivePrice()).thenReturn(1000L);
        when(milk.getName()).thenReturn("milk");

        when(bread.getArticleNumber()).thenReturn(1002);
        when(bread.getDiscountType()).thenReturn("4FOR3");
        when(bread.getPrice()).thenReturn(1500L);
        when(bread.getInclusivePrice()).thenReturn(1500L);
        when(bread.getName()).thenReturn("bread");

        when(oboy.getArticleNumber()).thenReturn(1003);
        when(oboy.getDiscountType()).thenReturn("5FOR4");
        when(oboy.getPrice()).thenReturn(2000L);
        when(oboy.getInclusivePrice()).thenReturn(2000L);
        when(oboy.getName()).thenReturn("oboy");
    }

    @Test
    void discountCheck3For2() {
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);

        expectedMap.put("3FOR2 on milk", 1000L);

        assertEquals(expectedMap, discount.getDiscountMap());
    }

    @Test
    void multipleMulitItemDiscountsInDifferentOrder() {
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(bread, 4);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(bread, 4);
        discount.checkMultiItemDiscount(bread, 4);
        discount.checkMultiItemDiscount(bread, 4);
        discount.checkMultiItemDiscount(milk, 3);

        expectedMap.put("4FOR3 on bread", 1500L);
        expectedMap.put("3FOR2 on milk", 1000L);

        assertEquals(expectedMap, discount.getDiscountMap());
    }

    /*@Test
    void discountTypeIs2KrOff() {
        assertEquals("2KR_OFF", oboy.getDiscountType());
    }*/

    @Test
    void removesItemFromCounterAfterCompletingThreeForTwo() {
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);

        assertTrue(discount.getItemCounterMap().isEmpty());
    }

    @Test
    void unusedItemsRemainInCounter() {
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);
        discount.checkMultiItemDiscount(milk, 3);

        expectedCounterMap.put(1001, 2L);

        assertTrue(discount.getItemCounterMap().containsKey(1001));
        assertEquals(expectedCounterMap, discount.getItemCounterMap());
    }

    @Test
    void returnsSeniorDiscount() {
        assertEquals(seniorDiscountPercentage, discount.getSeniorDiscount());
    }

    @Test
    void getAllDiscountsWithOrderList() {
        ArrayList<Item> orderList = new ArrayList<>(Arrays.asList(
                milk, bread, bread, milk, oboy, bread,
                milk, oboy, bread, bread, milk, oboy, oboy, milk, milk, oboy
        ));

        //6 milks complete TWO 3-for-2 groups, so the milk discount is 2 x 1000
        expectedMap.put("4FOR3 on bread", 1500L);
        expectedMap.put("5FOR4 on oboy", 2000L);
        expectedMap.put("3FOR2 on milk", 2000L);

        assertEquals(expectedMap, discount.getAllDiscounts(orderList));
    }
}

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

class DiscountTest {
    private final DiscountHandler discount = new DiscountHandler();
    private final Item milk = mock(Item.class);
    private final Item bread = mock(Item.class);
    private final Item oboy = mock(Item.class);
    private final int seniorDiscountPercentage = 10;

    private final Map<String, Long> expectedMap = new HashMap<>();
    private final Map<Item, Long> expectedCounterMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        expectedMap.clear();
        expectedCounterMap.clear();

        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000L);
        when(milk.getName()).thenReturn("milk");

        when(bread.getDiscountType()).thenReturn("3FOR2");
        when(bread.getPrice()).thenReturn(1500L);
        when(bread.getName()).thenReturn("bread");

        when(oboy.getDiscountType()).thenReturn("2KR_OFF");
        when(oboy.getPrice()).thenReturn(2000L);
        when(oboy.getName()).thenReturn("oboy");
    }

    @Test
    void discountCheck3For2() {
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);

        expectedMap.put("3FOR2 on milk", 1000L);

        assertEquals(expectedMap, discount.getDiscountMap());
    }

    @Test
    void multiple3For2DiscountsInDifferentOrder() {
        discount.check3for2Discount(milk);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(milk);

        expectedMap.put("3FOR2 on bread", 1500L);
        expectedMap.put("3FOR2 on milk", 1000L);

        assertEquals(expectedMap, discount.getDiscountMap());
    }

    @Test
    void discountTypeIs2KrOff() {
        assertEquals("2KR_OFF", oboy.getDiscountType());
    }

    @Test
    void removesItemFromCounterAfterCompletingThreeForTwo() {
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);

        assertTrue(discount.getItemCounterMap().isEmpty());
    }

    @Test
    void unusedItemsRemainInCounter() {
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);

        expectedCounterMap.put(milk, 2L);

        assertTrue(discount.getItemCounterMap().containsKey(milk));
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
                milk, bread, bread, milk, milk, milk
        ));

        expectedMap.put("3FOR2 on bread", 1500L);
        expectedMap.put("2KR_OFF on oboy", 2L);
        expectedMap.put("3FOR2 on milk", 1000L);

        assertEquals(expectedMap, discount.getAllDiscounts(orderList));
    }
}

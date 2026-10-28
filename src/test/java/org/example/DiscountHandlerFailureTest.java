package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;

// Tests that prove two failure modes in DiscountHandler before we fix them.
// These are written first (RED) as part of the discount decision table work.
class DiscountHandlerFailureTest {

    // Failure mode 1: an item with no discount type (discountType == null,
    // which is what every article from ArticleCatalogLoader has) crashes
    // getAllDiscounts with a NullPointerException, because switch(null)
    // throws in Java.
    @Test
    void itemsWithoutDiscountTypeDoNotCrash() {
        DiscountHandler handler = new DiscountHandler();

        Item bread = mock(Item.class);
        when(bread.getDiscountType()).thenReturn(null);

        Item milk = mock(Item.class);
        when(milk.getDiscountType()).thenReturn(null);

        ArrayList<Item> orderList = new ArrayList<>(Arrays.asList(bread, milk));

        Map<String, Long> result = handler.getAllDiscounts(orderList);

        assertEquals(0, result.size());
    }

    // Failure mode 2: the discounts map is keyed by "3FOR2 on <name>", so a
    // second completed group of the same article overwrites the first entry
    // instead of adding to it. Six milks with 3-for-2 should give TWO free
    // milks (2000 ore), but the current code only records one (1000 ore).
    @Test
    void sixItemsWithThreeForTwoGiveTwoDiscounts() {
        DiscountHandler handler = new DiscountHandler();

        Item milk = mock(Item.class);
        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000L);
        when(milk.getName()).thenReturn("milk");

        ArrayList<Item> orderList = new ArrayList<>(
                Arrays.asList(milk, milk, milk, milk, milk, milk));

        Map<String, Long> result = handler.getAllDiscounts(orderList);

        long totalDiscount = 0;
        for (Long value : result.values()) {
            totalDiscount += value;
        }

        assertEquals(2000, totalDiscount);
    }

    // Failure mode 3: the handler keeps its maps between getAllDiscounts calls,
    // so a second occasion re-adds the first occasion's discounts and leftover
    // partial groups leak from one customer's basket into the next.
    @Test
    void getAllDiscountsDoesNotAccumulateAcrossCalls() {
        DiscountHandler handler = new DiscountHandler();

        Item milk = mock(Item.class);
        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000L);
        when(milk.getInclusivePrice()).thenReturn(1000L);
        when(milk.getName()).thenReturn("milk");

        ArrayList<Item> threeMilks = new ArrayList<>(
                Arrays.asList(milk, milk, milk));

        handler.getAllDiscounts(threeMilks);
        Map<String, Long> secondCall = handler.getAllDiscounts(threeMilks);

        // the second call must report only its own basket: one free milk
        assertEquals(1000L, secondCall.get("3FOR2 on milk"));
    }

    // Failure mode 4: the item counter is keyed by object identity, so three
    // SEPARATE Item instances that represent the same article (same article
    // number) never form a discount group. Keying on article number instead
    // makes counting independent of how the instances were created.
    @Test
    void separateInstancesOfSameArticleFormOneDiscountGroup() {
        DiscountHandler handler = new DiscountHandler();

        ArrayList<Item> threeSeparateMilks = new ArrayList<>(Arrays.asList(
                milkInstance(), milkInstance(), milkInstance()));

        Map<String, Long> result = handler.getAllDiscounts(threeSeparateMilks);

        assertEquals(1000L, result.get("3FOR2 on milk"));
    }

    private Item milkInstance() {
        Item milk = mock(Item.class);
        when(milk.getArticleNumber()).thenReturn(1001);
        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000L);
        when(milk.getInclusivePrice()).thenReturn(1000L);
        when(milk.getName()).thenReturn("milk");
        return milk;
    }
}

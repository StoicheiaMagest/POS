package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountTest {
    private final Discount discount = new Discount();

    private Basket basket = mock(Basket.class);
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

    @Test
    void Items_without_discount_give_no_discounts(){
        when(item.getArticleNumber()).thenReturn(101);
        ArrayList<Item> orderList = new ArrayList<>();
        orderList.add(item);

        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(orderList));
    }

    //
    @Test
    void Discount_total_is_returned(){
        when(item.getArticleNumber()).thenReturn(222);
        when(item.getName()).thenReturn("Bread");
        when(item.getPrice()).thenReturn(15);

        ArrayList<Item> orderList = new ArrayList<>();
        orderList.add(item);
        orderList.add(item);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on "+item.getName(), 400L);

        assertEquals(expected, discount.getAllDiscounts(orderList));
    }

    @Test
    void Bread_discount_is_200_cents_for_one_bread(){
        when(item.getArticleNumber()).thenReturn(222);
        when(item.getName()).thenReturn("Bread");
        ArrayList<Item> orderList = new ArrayList<>();
        orderList.add(item);

        Map<String, Long> expected = new HashMap<>();
        expected.put("2 kr off on Bread", 200L);

        assertEquals(expected, discount.getAllDiscounts(orderList));
    }

    private ArrayList<Item> milks(int count) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new UnitPricedArticle("Milk", 1000, Vat.FOOD, 111));
        }
        return list;
    }

    @Test
    void Three_milks_give_one_free_milk(){
        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 1120L);

        assertEquals(expected, discount.getAllDiscounts(milks(3)));
    }

    @Test
    void Seven_milks_give_two_free_milks(){
        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 2240L);

        assertEquals(expected, discount.getAllDiscounts(milks(7)));
    }

    @Test
    void Two_milks_give_no_discount(){
        assertEquals(new HashMap<String, Long>(), discount.getAllDiscounts(milks(2)));
    }

    @Test
    void Milk_and_bread_discounts_are_combined(){
        ArrayList<Item> orderList = milks(3);
        orderList.add(new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));

        Map<String, Long> expected = new HashMap<>();
        expected.put("3 for 2 Milk", 1120L);
        expected.put("2 kr off on Bread", 200L);

        assertEquals(expected, discount.getAllDiscounts(orderList));
    }

    @Test
    void Discount_lines_carry_the_vat_rate_of_the_article(){
        List<DiscountLine> expected = List.of(new DiscountLine("3 for 2 Milk", 1120L, Vat.FOOD));

        assertEquals(expected, discount.getDiscountLines(milks(3)));
    }

    @Test
    void Discount_lines_combine_bread_and_milk(){
        ArrayList<Item> orderList = milks(3);
        orderList.add(new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));
        orderList.add(new UnitPricedArticle("Bread", 2000, Vat.FOOD, 222));

        List<DiscountLine> expected = List.of(
                new DiscountLine("2 kr off on Bread", 400L, Vat.FOOD),
                new DiscountLine("3 for 2 Milk", 1120L, Vat.FOOD));

        assertEquals(expected, discount.getDiscountLines(orderList));
    }

    @Test
    void Discount_lines_are_empty_without_discounts(){
        assertEquals(List.of(), discount.getDiscountLines(milks(2)));
    }

    @Test
    void Repeated_calls_do_not_accumulate_discounts(){
        when(item.getArticleNumber()).thenReturn(222);
        when(item.getName()).thenReturn("Bread");
        ArrayList<Item> orderList = new ArrayList<>();
        orderList.add(item);

        discount.getAllDiscounts(orderList);
        assertEquals(200L, discount.getAllDiscounts(orderList).get("2 kr off on Bread"));
    }
}

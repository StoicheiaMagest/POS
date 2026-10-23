package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
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
        expected.put("2 kr off on "+item.getName(), 2L);

        assertEquals(expected, discount.getAllDiscounts(orderList));
    }
    @Test 
    void Discount_for_200kr_purchace_is_20kr(){

        //when(basket.getTotalPrice()>=200);

        //discount.add20krDiscount();
        //assertEquals(20,discount.totalDiscountSum());
    }

}

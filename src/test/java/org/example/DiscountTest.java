package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscountTest {
    private final DiscountHandler discount = new DiscountHandler();

    //private Basket basket = mock(Basket.class);
    private Item item = mock(Item.class);
    private Item item2 = mock(Item.class);

    @Test 
    void Discount_Check_3_for_2(){
        when(item.getDiscountType()).thenReturn("3FOR2");
        when(item.getPrice()).thenReturn(10);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);
        
        Map<String, Long> expectMap = new HashMap<>();
        expectMap.put(item.getDiscountType(), (long)item.getPrice());

        assertEquals(expectMap, discount.getDiscountMap());
    }

    @Test 
    void Multiple_3_for_2_in_diffrent_order(){
        when(item.getDiscountType()).thenReturn("3FOR2");
        when(item.getPrice()).thenReturn(10);
        when(item2.getDiscountType()).thenReturn("3FOR2");
        when(item2.getPrice()).thenReturn(15);

        discount.check3for2Discount(item);
        discount.check3for2Discount(item2);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item2);
        discount.check3for2Discount(item2);
        discount.check3for2Discount(item);
        
        Map<String, Long> expectMap = new HashMap<>();
        expectMap.put(item2.getDiscountType(), (long)item2.getPrice());
        expectMap.put(item.getDiscountType(), (long)item.getPrice());

        assertEquals(expectMap,discount.getDiscountMap());
    }

    @Test
    void Discount_type_is_2p(){
        when(item.getDiscountType()).thenReturn("2KR_OFF");
        assertEquals("2KR_OFF", item.getDiscountType());
    }


}

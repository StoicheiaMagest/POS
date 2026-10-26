package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.HashMap;
import java.util.Map;

public class DiscountTest {
    private final DiscountHandler discount = new DiscountHandler();

    //private Basket basket = mock(Basket.class);
    private Item item = mock(Item.class);
    private Item item2 = mock(Item.class);

    @Test 
    void Discount_check_3_for_2(){
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
        when(item.getPrice()).thenReturn(1000);
        when(item2.getDiscountType()).thenReturn("3FOR2");
        when(item2.getPrice()).thenReturn(1500);

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

    @Test 
    void Removed_item_from_counter_after_complete(){
        when(item.getDiscountType()).thenReturn("3FOR2");
        when(item.getPrice()).thenReturn(1000);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);

        assertTrue(discount.getItemCounterMap().isEmpty());
    }

    @Test
    void Unused_item_left_in_counter(){
        when(item.getDiscountType()).thenReturn("3FOR2");
        when(item.getPrice()).thenReturn(1000);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);
        discount.check3for2Discount(item);


        Map<Item, Integer> expectMap = new HashMap<>();
        expectMap.put(item,item.getPrice());


        Map<String, Long> expectMap2 = new HashMap<>();
        expectMap2.put(item.getDiscountType(), (long)item.getPrice());

        assertEquals(expectMap, discount.getItemCounterMap());
        assertEquals(expectMap2, discount.getDiscountMap());
    }
    
    @Test 
    void Get_Senior_Discount(){
        assertEquals(discount.getSeniorDiscount(), 10);
    }

    @Test 
    void getAllDiscounts_called_with_orderlist(){

    }
}

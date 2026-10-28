package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DiscountTest {
    private final DiscountHandler discount = new DiscountHandler();
    private Item milk = mock(Item.class);
    private Item bread = mock(Item.class);
    private Item oboy = mock(Item.class);
    private int seniorDiscountProcentage = 10;
    Map<String, Long> expectedMap = new HashMap<>();
    Map<Item, Integer> expectedCounterMap = new HashMap<>();
    
    @BeforeEach
    void setUp(){
        when(milk.getDiscountType()).thenReturn("3FOR2");
        when(milk.getPrice()).thenReturn(1000l);
        when(milk.getName()).thenReturn("milk");
        when(bread.getDiscountType()).thenReturn("3FOR2");
        when(bread.getPrice()).thenReturn(1500l);
        when(bread.getName()).thenReturn("bread");
        when(oboy.getDiscountType()).thenReturn("2KR_OFF");
        when(oboy.getPrice()).thenReturn(2000l);
        when(oboy.getName()).thenReturn("oboy");
    }

    @Test 
    void Discount_check_3_for_2(){
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        
        expectedMap.put(milk.getDiscountType() +" on "+milk.getName(), (long)milk.getPrice());
        assertEquals(expectedMap, discount.getDiscountMap());
        //System.err.println(discount.getDiscountMap());
    }

    @Test 
    void Multiple_3_for_2_in_diffrent_order(){
        discount.check3for2Discount(milk);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(bread);
        discount.check3for2Discount(milk);
        
        expectedMap.put(bread.getDiscountType() +" on "+bread.getName(), (long)bread.getPrice());
        expectedMap.put(milk.getDiscountType() +" on "+milk.getName(), (long)milk.getPrice());

        assertEquals(expectedMap,discount.getDiscountMap());
        //System.err.println(discount.getDiscountMap());
    }

    @Test
    void Discount_type_is_2p(){
        assertEquals("2KR_OFF", oboy.getDiscountType());
    }

    @Test 
    void Removed_item_from_counter_after_complete(){
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);

        assertTrue(discount.getItemCounterMap().isEmpty());
    }

    @Test
    void Unused_item_left_in_counter(){
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);
        discount.check3for2Discount(milk);

        expectedCounterMap.put(milk,2);

        assertTrue(discount.getItemCounterMap().containsKey(milk));
        assertEquals(expectedCounterMap, discount.getItemCounterMap());
    }
    
    @Test 
    void Get_Senior_Discount(){
        assertEquals(discount.getSeniorDiscount(), seniorDiscountProcentage);
    }

    @Test 
    void getAllDiscounts_called_with_orderlist(){
        //make big orderlist and send to getAllDiscounts check if list is correct
        ArrayList<Item> orderListen = new ArrayList<>(Arrays.asList(milk,bread,bread,milk,oboy,bread,milk,bread,bread,milk,milk,milk));
    
        expectedMap.put(bread.getDiscountType() +" on "+bread.getName(), (long)bread.getPrice());
        expectedMap.put(oboy.getDiscountType() +" on "+oboy.getName(), 2l);
        expectedMap.put(milk.getDiscountType() +" on "+milk.getName(), (long)milk.getPrice());
        expectedMap.put(milk.getDiscountType() +" on "+milk.getName(), (long)milk.getPrice());
        //System.out.println("test 6 "+expectedMap +"  "+ discount.getAllDiscounts(orderListen));
        
        
        assertEquals(expectedMap, discount.getAllDiscounts(orderListen));
        System.err.println(discount.getAllDiscounts(orderListen));
    }
}

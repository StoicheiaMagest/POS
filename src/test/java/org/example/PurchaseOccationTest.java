package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseOccationTest {

    
    Account account = mock(Account.class);
    Basket basket = mock(Basket.class);
    Discount discount = mock(Discount.class);
    Membership membership = mock(Membership.class);
    PurchaseOccation order = new PurchaseOccation(account, basket, discount);
    

    @Test
    void total_cost_should_be_zero_with_an_empty_basket() {
        assertEquals(0, order.getTotalCost(), "Total cost of the purchase should be zero");
    }

    @Test
    void total_cost_should_consist_with_basket() {
        when(basket.getTotalPrice()).thenReturn(1000.0);

        assertEquals(1000.0, order.getTotalCost(), 0.001, "Total price for the purchase should be 1000");
    }

    @Test
    void purchaseOccation_should_return_the_right_membership() {
        order.setMembersip(membership);

        assertEquals(membership, order.getMembership(), "Correct membership should be returned");
    }

    @Test
    void getBasket_should_return_a_copy_of_list() {

    }

    @Test
    void a_senior_discount_should_change_total_cost_of_purchase() {
        when(basket.getTotalPrice()).thenReturn(1000.0);
        when(discount.getSeniorDiscount()).thenReturn(0.10);
        order.useSeniorDiscount();

        assertEquals(800.0, order.getTotalCost(), 0.001, "Total price for the purchase should be 800");
    }

    @Test
    void applying_group_discount_should_change_total_cost_of_purchase() {
        when(discount.getGroupDiscount()).thenReturn(/*map med discounts??? */);
        order.applyGroupDiscount();

        assertEquals(800, order.getTotalCost(), 0.001, "Total price for the purchase should be 800");
    }

    @Test total_cost_of_purchase_should_not_change_if_group_discount_is_absent() {
        when(discount.getGroupDiscount()).thenReturn(/*null eller tom lista? */);

        assertFalse(order.applyGroupDiscount(), "If false, total cost should remain unchanged");
    }





    
    /* 
    @Test
    void cannot_scan_empty_basket() {
        Basket basket = new Basket();
        PurchaseOccation order = new PurchaseOccation(account, basket);

        assertThrows(IllegalArgumentException.class, () -> {
            //order.scanItems(basket);
        });
    }

    @Test
    void scanItems_return_correct_totalCost_for_one_item() {
        Item item = new UnitPricedArticle("Coffee", 2500, Vat.FOOD, 1);
        Basket basket = new Basket();
        PurchaseOccation order = new PurchaseOccation(account, basket);

        basket.addItem(item);
        //order.scanItems(basket);
        int expectedTotal = item.getPrice();

        assertEquals(expectedTotal, order.getTotalCost());
    }
    */
    
    //logik basket -> order
    //logiken order -> reciept


}

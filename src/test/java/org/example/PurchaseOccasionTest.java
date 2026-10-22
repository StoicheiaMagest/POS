package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOccasionTest {

    Account account = mock(Account.class);
    Basket basket = mock(Basket.class);
    Discount discount = mock(Discount.class);
    Membership membership = mock(Membership.class);
    PurchaseOccation order = new PurchaseOccation(account, basket, discount);

    @Test
    void receiptShowsZeroTotalForEmptyBasket() {
        Basket basket = new Basket();
        PurchaseOccasion purchase = createPurchase(basket);

        assertEquals(
                "Kvitto\nAntal varor: 0\nTotalt: 0,00 SEK",
                purchase.printReceipt()
        );
    }

    @Test
    void receiptShowsTotalForOneItem() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(9999));

        PurchaseOccasion purchase = createPurchase(basket);

        assertEquals(
                "Kvitto\nAntal varor: 1\nTotalt: 99,99 SEK",
                purchase.printReceipt()
        );
    }

    @Test
    void receiptShowsSumForMultipleItems() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(1250));
        basket.addItem(itemWithPrice(250));

        PurchaseOccasion purchase = createPurchase(basket);

        assertEquals(
                "Kvitto\nAntal varor: 2\nTotalt: 15,00 SEK",
                purchase.printReceipt()
        );
    }

    private Item itemWithPrice(int priceInOre) {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(priceInOre);
        return item;
    }

    private PurchaseOccasion createPurchase(Basket basket) {
        Customer customer =
                new Customer("Test Customer", "19900101-1234", false, false);
        Account account = mock(Account.class);

        return new PurchaseOccasion(basket, customer, account);
    }

    @Test
    void total_cost_should_be_zero_with_an_empty_basket() {
        assertEquals(0, order.getTotalCost(), "Total cost of the purchase should be zero");
    }

    @Test
    void total_cost_should_consist_with_basket() {
        when(basket.getTotalPrice()).thenReturn(1000);

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
}

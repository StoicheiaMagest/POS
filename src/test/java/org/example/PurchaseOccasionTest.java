package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOccasionTest {

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

        //misslyckas för item behöver hashcode?
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

    @Test
    void a_senior_discount_should_change_total_cost_of_purchase() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(10000));

        DiscountHandler discount = mock(DiscountHandler.class);
        when(DiscountHandler.getSeniorDiscount()).thenReturn((long) 10);

        PurchaseOccasion purchase = createPurchase(basket);
        if(purchase.getSeniorStatus()) {
            purchase.applySeniorDiscount();
        }

        assertEquals(9000, purchase.getTotalPriceForPurchace(), 0.001, "Total price should adhere to discount");
    }

    @Test
    void getMembershipStatusShouldReturnTrueIfMember() {
        Customer customer = mock(Customer.class);
        when(customer.getMembershipStatus()).thenReturn(true);
        Account account = mock(Account.class);
        Basket basket = mock(Basket.class);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, account);
        assertTrue(purchase.getMembershipStatus());
    }

    @Test 
    void getSeniorStatusShouldReturnTrueIfSenior() {
        Customer customer = mock(Customer.class);
        when(customer.getSeniorStatus()).thenReturn(true);
        Account account = mock(Account.class);
        Basket basket = mock(Basket.class);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, account);
        assertTrue(purchase.getSeniorStatus());
    }

    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        return item;
    }

    private PurchaseOccasion createPurchase(Basket basket) {
        Customer customer =
                new Customer("Test Customer", "19900101-1234", false, false);
        Account account = mock(Account.class);

        return new PurchaseOccasion(basket, customer, account);
    }
}

//package org.example;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.Test;
//
//class PurchaseOccasionDiscountIntegrationTest {
//
//    @Test
//    void totalForThreeItemsWithoutConfiguredDiscount() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(10000));
//        basket.addItem(itemWithPrice(10000));
//        basket.addItem(itemWithPrice(10000));
//
//        Customer customer =
//                new Customer("Test Customer", "19900101-1234", false, false);
//        Account account = mock(Account.class);
//
//        PurchaseOccasion purchase =
//                new PurchaseOccasion(basket, customer, account);
//
//        assertEquals(
//                30000,
//                purchase.getTotalPriceForPurchace(),
//                0.001
//        );
//    }
//
//    private Item itemWithPrice(long priceInOre) {
//        Item item = mock(Item.class);
//        when(item.getPrice()).thenReturn(priceInOre);
//        when(item.getInclusivePrice()).thenReturn(priceInOre);
//        return item;
//    }
//}

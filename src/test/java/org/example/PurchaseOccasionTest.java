//package org.example;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//
//class PurchaseOccasionTest {
//
//    private final Account account = mock(Account.class);
//
//    private Item itemWithPrice(long priceInOre) {
//        Item item = mock(Item.class);
//
//        // Stub both in case the production code uses either method.
//        when(item.getPrice()).thenReturn(priceInOre);
//        when(item.getInclusivePrice()).thenReturn(priceInOre);
//
//        return item;
//    }
//
//    private PurchaseOccasion createPurchase(Basket basket, Customer customer) {
//        return new PurchaseOccasion(basket, customer, account);
//    }
//
//    @Test
//    void receiptShowsZeroTotalForEmptyBasket() {
//        Basket basket = new Basket();
//        Customer customer = new Customer(
//                "Regular Customer", "19900101-1234", false, false);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 0\nRabatter: None\nTotalt: 0,00 SEK",
//                purchase.printReceipt()
//        );
//    }
//
//    @Test
//    void receiptShowsTotalForOneItem() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(9999));
//
//        Customer customer = new Customer(
//                "Regular Customer", "19900101-1234", false, false);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 1\nRabatter: None\nTotalt: 99,99 SEK",
//                purchase.printReceipt()
//        );
//    }
//
//    @Test
//    void receiptShowsSumForMultipleItems() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(1250));
//        basket.addItem(itemWithPrice(250));
//
//        Customer customer = new Customer(
//                "Regular Customer", "19900101-1234", false, false);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 2\nRabatter: None\nTotalt: 15,00 SEK",
//                purchase.printReceipt()
//        );
//    }
//
//    @Test
//    void constructorThrowsWhenCustomerIsNull() {
//        Basket basket = new Basket();
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> createPurchase(basket, null)
//        );
//    }
//
//    @Test
//    void getMembershipStatusReturnsTrueForMember() {
//        Basket basket = new Basket();
//        Customer customer = mock(Customer.class);
//        when(customer.getMembershipStatus()).thenReturn(true);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertTrue(purchase.getMembershipStatus());
//    }
//
//    @Test
//    void getSeniorStatusReturnsTrueForSenior() {
//        Basket basket = new Basket();
//        Customer customer = mock(Customer.class);
//        when(customer.getSeniorStatus()).thenReturn(true);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertTrue(purchase.getSeniorStatus());
//    }
//
//    @Test
//    void membershipAndSeniorStatusAreFalseForRegularCustomer() {
//        Basket basket = new Basket();
//        Customer customer = mock(Customer.class);
//        when(customer.getMembershipStatus()).thenReturn(false);
//        when(customer.getSeniorStatus()).thenReturn(false);
//
//        PurchaseOccasion purchase = createPurchase(basket, customer);
//
//        assertFalse(purchase.getMembershipStatus());
//        assertFalse(purchase.getSeniorStatus());
//    }
//    @Test
//    void seniorDiscountChangesTotalCost() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(10000));
//
//        Customer senior =
//                new Customer("Senior Customer", "19400101-1234", false, true);
//        Account account = mock(Account.class);
//
//        try (MockedStatic<DiscountHandler> discountHandler =
//                     mockStatic(DiscountHandler.class)) {
//            discountHandler.when(DiscountHandler::getSeniorDiscount)
//                    .thenReturn(10L);
//
//            PurchaseOccasion purchase =
//                    new PurchaseOccasion(basket, senior, account);
//
//            purchase.applySeniorDiscount();
//
//            assertEquals(
//                    9000,
//                    purchase.getTotalPriceForPurchace(),
//                    0.001
//            );
//        }
//    }
//
//}

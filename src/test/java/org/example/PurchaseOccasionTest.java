package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseOccasionTest {

    @Test
    void scanAllGroupsArticlesAndReportsRunningInclusiveTotals() {
        // Arrange: assortment from spec §5 (soy-milk 34.96, juice 2×29.95,
        // apple 300 g at 24.90/kg inclusive)
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        basket.addItem(registry.findByName("orange-juice"));
        basket.addItem(registry.findByName("orange-juice"));
        basket.addItem(new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple"));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Test Customer", "19900101-1234"),
                new Account(0));

        // Act
        List<ScanLine> lines = purchase.scanAll();

        // Assert: grouped scan lines with cumulative inclusive totals
        assertEquals(3, lines.size());
        assertEquals("1", lines.get(0).getQuantity());
        assertEquals("Alpro Soymilk", lines.get(0).getDescription());
        assertEquals(3496, lines.get(0).getRunningTotalCents());
        assertEquals("2", lines.get(1).getQuantity());
        assertEquals(3496 + 5990, lines.get(1).getRunningTotalCents());
        assertEquals("300g", lines.get(2).getQuantity());
        assertEquals("Apple", lines.get(2).getDescription());
        long appleLine = new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple").getInclusivePrice();
        assertEquals(3496 + 5990 + appleLine,
                lines.get(2).getRunningTotalCents());
    }

    @Test
    void scanningTwiceIsRefused() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Test Customer", "19900101-1234"),
                new Account(0));
        purchase.scanAll();

        assertTrue(purchase.isScanned());
        assertThrows(IllegalStateException.class, purchase::scanAll);
    }

    @Test
    void memberDiscountReducesAmountDueByTwentyPercentHalfUp() {
        // Arrange: soy-milk 34.96 + juice 29.95 = 64.91 due
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        basket.addItem(registry.findByName("orange-juice"));
        Customer member = new Customer("Member", "19900101-1234");
        member.joinMembership(java.time.LocalDate.of(1990, 1, 1), "M000001");
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket, member, new Account(0));
        purchase.scanAll();

        // Assert pre-state
        assertEquals(6491, purchase.getAmountDueCents());

        // Act
        DiscountResult result = purchase.applyMembershipDiscount();

        // Assert: 20% of 6491 = 1298.2 -> half-up 1298 off -> 5193 due
        assertEquals(DiscountResult.APPLIED, result);
        assertEquals(5193, purchase.getAmountDueCents());
    }

    @Test
    void discountIsRefusedForNonMembers() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Guest", "19900101-1234"), new Account(0));
        purchase.scanAll();

        DiscountResult result = purchase.applyMembershipDiscount();

        assertEquals(DiscountResult.NOT_A_MEMBER, result);
        assertEquals(3496, purchase.getAmountDueCents());
    }

    @Test
    void discountCanOnlyBeAppliedOnce() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        Customer member = new Customer("Member", "19900101-1234");
        member.joinMembership(java.time.LocalDate.of(1990, 1, 1), "M000001");
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket, member, new Account(0));
        purchase.scanAll();
        purchase.applyMembershipDiscount();

        DiscountResult second = purchase.applyMembershipDiscount();

        assertEquals(DiscountResult.ALREADY_APPLIED, second);
        assertEquals(2797, purchase.getAmountDueCents(),
                "second attempt must not discount again");
    }

    @Test
    void fullCardPaymentTransfersDueAmountToStore() {
        // Arrange: due 34.96, card holds 500.00
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        Account storeAccount = new Account(0);
        Customer customer = new Customer("Payer", "19900101-1234");
        PaymentCard card = new PaymentCard(7, "Visa", new Account(50000));
        customer.addCard(card);
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket, customer, storeAccount);
        purchase.scanAll();

        // Act
        PaymentResult result = purchase.pay(card, purchase.getAmountDueCents());

        // Assert
        assertEquals(PaymentResult.SUCCESS, result);
        assertEquals(0, purchase.getAmountDueCents());
        assertTrue(purchase.isPaymentComplete());
        assertEquals(3496, storeAccount.getBalance());
        assertEquals(50000 - 3496, card.getBalance());
    }

    @Test
    void insufficientCardBalanceTransfersNothing() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        Account storeAccount = new Account(0);
        PaymentCard card = new PaymentCard(8, "Amex", new Account(100));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Poor", "19900101-1234"), storeAccount);
        purchase.scanAll();

        PaymentResult result = purchase.pay(card, 3496);

        assertEquals(PaymentResult.INSUFFICIENT_BALANCE, result);
        assertEquals(3496, purchase.getAmountDueCents());
        assertEquals(0, storeAccount.getBalance());
        assertEquals(100, card.getBalance());
    }

    @Test
    void splitPaymentAcrossTwoCardsCompletesOccasion() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk")); // due 34.96
        Account storeAccount = new Account(0);
        PaymentCard first = new PaymentCard(1, "Visa", new Account(2000));
        PaymentCard second = new PaymentCard(2, "MasterCard", new Account(9999));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Splitter", "19900101-1234"), storeAccount);
        purchase.scanAll();

        assertEquals(PaymentResult.SUCCESS, purchase.pay(first, 2000));
        assertEquals(1496, purchase.getAmountDueCents());
        assertEquals(PaymentResult.SUCCESS, purchase.pay(second, 1496));

        assertTrue(purchase.isPaymentComplete());
        assertEquals(3496, storeAccount.getBalance());
        assertEquals(0, first.getBalance());
        assertEquals(9999 - 1496, second.getBalance());
    }

    @Test
    void discountIsRefusedOncePaymentHasStarted() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        Customer member = new Customer("Member", "19900101-1234");
        member.joinMembership(java.time.LocalDate.of(1990, 1, 1), "M000001");
        PaymentCard card = new PaymentCard(3, "Visa", new Account(50000));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket, member, new Account(0));
        purchase.scanAll();
        purchase.pay(card, 100);

        DiscountResult result = purchase.applyMembershipDiscount();

        assertEquals(DiscountResult.PAYMENT_STARTED, result);
        assertEquals(3396, purchase.getAmountDueCents(),
                "only the 100-cent payment reduced the due");
    }
}

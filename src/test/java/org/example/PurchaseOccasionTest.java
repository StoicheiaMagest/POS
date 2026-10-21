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
}

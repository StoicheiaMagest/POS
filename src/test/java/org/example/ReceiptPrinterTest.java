package org.example;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Tests for printing a receipt through the Java Print Service API (javax.print).
 * The printer is a hand-written fake implementing javax.print.PrintService,
 * so no real printer or spooler is involved.
 */
public class ReceiptPrinterTest {

    private static PurchaseOccasion simplePurchase() {
        Store store = new Store(new ArticleRegistry(), new Account(),
                new DiscountHandler(), new BarcodeScanner(new ArticleRegistry()));
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Coffee", 4900L, Vat.FOOD, 114));
        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        return new PurchaseOccasion(basket, customer, store);
    }

    private static Receipt simpleReceipt() {
        return new Receipt(1, LocalDateTime.of(2026, 10, 29, 10, 15), "Test Store", simplePurchase());
    }

    @Test
    void printing_a_receipt_sends_exactly_one_print_job() throws Exception {
        FakePrinter printer = new FakePrinter();
        ReceiptPrinter receiptPrinter = new ReceiptPrinter(printer);

        receiptPrinter.print(simpleReceipt());

        assertEquals(1, printer.printedJobs().size());
    }

    @Test
    void the_printed_job_contains_the_receipt_text() throws Exception {
        FakePrinter printer = new FakePrinter();
        ReceiptPrinter receiptPrinter = new ReceiptPrinter(printer);
        Receipt receipt = simpleReceipt();

        receiptPrinter.print(receipt);

        assertEquals(receipt.getReceiptText(), printer.printedJobs().get(0));
    }

    @Test
    void constructing_a_receipt_printer_without_a_printer_fails_fast() {
        assertThrows(NullPointerException.class, () -> new ReceiptPrinter(null));
    }

    @Test
    void a_printed_paid_receipt_with_discounts_shows_all_its_rows() throws Exception {
        // A senior member with a coupon pays for coffee and bread.
        Store store = new Store(new ArticleRegistry(), new Account(),
                new DiscountHandler(), new BarcodeScanner(new ArticleRegistry()));
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Coffee", 4900L, Vat.FOOD, 114));
        basket.addItem(new UnitPricedArticle("Bread", 2500L, Vat.FOOD, 222));
        Customer customer = new Customer("Greta", "19400101-1234", true, true);
        customer.addCoupon(new Coupon("20krOff", 2000L, LocalDateTime.now().plusDays(1)));
        customer.setCard(new Card(5555L, new Account(100000L)));
        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);
        purchase.applyDiscounts();
        assertTrue(purchase.payPurchase());

        FakePrinter printer = new FakePrinter();
        Receipt receipt = new Receipt(7, LocalDateTime.of(2026, 10, 29, 12, 0), "Test Store", purchase);
        new ReceiptPrinter(printer).print(receipt);

        String printed = printer.printedJobs().get(0);
        assertTrue(printed.contains("Kvittonr:    7"), "receipt number missing");
        assertTrue(printed.contains("Coffee"), "item row missing");
        assertTrue(printed.contains("Delsumma"), "subtotal row missing");
        assertTrue(printed.contains("Kuponger"), "coupon discount row missing");
        assertTrue(printed.contains("Seniorrabatt"), "senior discount row missing");
        assertTrue(printed.contains("BETALT"), "paid marker missing");
    }
}

package org.example;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

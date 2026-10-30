package org.example;

import java.time.LocalDateTime;
import java.util.List;

import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReceiptPrinterTest {

    private static PurchaseOccasion simplePurchase() {
        Store store = new Store(
                new ArticleRegistry(),
                new Account(),
                new DiscountHandler(),
                new BarcodeScanner(new ArticleRegistry()));

        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Coffee", 4900L, Vat.FOOD, 114));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        return new PurchaseOccasion(basket, customer, store);
    }

    private static Receipt simpleReceipt() {
        return new Receipt(
                1,
                LocalDateTime.of(2026, 10, 29, 10, 15),
                "Test Store",
                simplePurchase());
    }

    private static String printAndCaptureText(
            PrintService printer,
            DocPrintJob printJob,
            Receipt receipt) throws Exception {

        when(printer.createPrintJob()).thenReturn(printJob);

        new ReceiptPrinter(printer).print(receipt);

        ArgumentCaptor<Doc> docCaptor = ArgumentCaptor.forClass(Doc.class);
        verify(printJob).print(docCaptor.capture(), isNull());

        return (String) docCaptor.getValue().getPrintData();
    }

    @Test
    void printing_a_receipt_sends_exactly_one_print_job() throws Exception {
        PrintService printer = mock(PrintService.class);
        DocPrintJob printJob = mock(DocPrintJob.class);
        when(printer.createPrintJob()).thenReturn(printJob);

        new ReceiptPrinter(printer).print(simpleReceipt());

        verify(printer, times(1)).createPrintJob();
        verify(printJob, times(1)).print(any(Doc.class), isNull());
    }

    @Test
    void the_printed_job_contains_the_receipt_text() throws Exception {
        PrintService printer = mock(PrintService.class);
        DocPrintJob printJob = mock(DocPrintJob.class);
        Receipt receipt = simpleReceipt();

        String printed = printAndCaptureText(printer, printJob, receipt);

        assertEquals(receipt.getReceiptText(), printed);
    }

    @Test
    void constructing_a_receipt_printer_without_a_printer_fails_fast() {
        assertThrows(NullPointerException.class, () -> new ReceiptPrinter(null));
    }

    @Test
    void a_printed_paid_receipt_with_discounts_shows_all_its_rows() throws Exception {
        // A senior member with a coupon pays for coffee and bread.
        Store store = new Store(
                new ArticleRegistry(),
                new Account(),
                new DiscountHandler(),
                new BarcodeScanner(new ArticleRegistry()));

        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Coffee", 4900L, Vat.FOOD, 114));
        basket.addItem(new UnitPricedArticle("Bread", 2500L, Vat.FOOD, 222));

        Customer customer = new Customer("Greta", "19400101-1234", true, true);
        customer.addCoupon(new Coupon(
                "20krOff",
                2000L,
                LocalDateTime.now().plusDays(1)));
        customer.setCard(new Card(5555L, new Account(100000L)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);
        purchase.applyDiscounts();
        assertTrue(purchase.payPurchase());

        Receipt receipt = new Receipt(
                7,
                LocalDateTime.of(2026, 10, 29, 12, 0),
                "Test Store",
                purchase);

        PrintService printer = mock(PrintService.class);
        DocPrintJob printJob = mock(DocPrintJob.class);
        String printed = printAndCaptureText(printer, printJob, receipt);

        assertTrue(printed.contains("Kvittonr:    7"), "receipt number missing");
        assertTrue(printed.contains("Coffee"), "item row missing");
        assertTrue(printed.contains("Delsumma"), "subtotal row missing");
        assertTrue(printed.contains("Kuponger"), "coupon discount row missing");
        assertTrue(printed.contains("Seniorrabatt"), "senior discount row missing");
        assertTrue(printed.contains("BETALT"), "paid marker missing");
    }

    @Test
    void several_receipts_come_out_of_the_printer_in_print_order() throws Exception {
        PrintService printer = mock(PrintService.class);
        DocPrintJob printJob = mock(DocPrintJob.class);
        when(printer.createPrintJob()).thenReturn(printJob);

        ReceiptPrinter receiptPrinter = new ReceiptPrinter(printer);

        for (int number = 1; number <= 3; number++) {
            Receipt receipt = new Receipt(
                    number,
                    LocalDateTime.of(2026, 10, 29, 10, 15),
                    "Test Store",
                    simplePurchase());
            receiptPrinter.print(receipt);
        }

        verify(printer, times(3)).createPrintJob();

        ArgumentCaptor<Doc> docCaptor = ArgumentCaptor.forClass(Doc.class);
        verify(printJob, times(3)).print(docCaptor.capture(), isNull());

        List<Doc> documents = docCaptor.getAllValues();
        assertEquals(3, documents.size());

        for (int i = 0; i < documents.size(); i++) {
            String printed = (String) documents.get(i).getPrintData();
            assertTrue(
                    printed.contains("Kvittonr:    " + (i + 1)),
                    "job " + i + " is not receipt " + (i + 1));
        }
    }

    @Test
    void an_unpaid_receipt_prints_without_the_paid_marker() throws Exception {
        // simplePurchase is never paid, so the receipt must not claim BETALT.
        PrintService printer = mock(PrintService.class);
        DocPrintJob printJob = mock(DocPrintJob.class);

        String printed = printAndCaptureText(printer, printJob, simpleReceipt());

        assertTrue(printed.contains("SUMMA"), "total row missing");
        assertFalse(printed.contains("BETALT"), "unpaid receipt claims to be paid");
    }
}

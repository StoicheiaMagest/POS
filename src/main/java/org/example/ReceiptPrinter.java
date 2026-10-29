package org.example;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintException;
import javax.print.SimpleDoc;

/*
 * Sends a finished receipt to a printer through the Java Print Service API
 * (javax.print). The printer is injected, so production can hand it a real
 * printer from PrintServiceLookup while tests use the fake one.
 */
public class ReceiptPrinter {

    private static final DocFlavor TEXT = new DocFlavor("text/plain", "java.lang.String");

    private final PrintService printer;

    public ReceiptPrinter(PrintService printer) {
        this.printer = printer;
    }

    /** Sends the receipt to the printer as one print job. */
    public void print(Receipt receipt) throws PrintException {
        Doc document = new SimpleDoc(receipt.getReceiptText(), TEXT, null);
        printer.createPrintJob().print(document, null);
    }
}

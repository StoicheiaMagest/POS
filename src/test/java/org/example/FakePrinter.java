package org.example;

import java.util.ArrayList;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;
import javax.print.event.PrintServiceAttributeListener;

/*
 * A printer mock written with plain Java: it implements the JDK's own
 * javax.print.PrintService interface and, instead of sending anything to a
 * spooler, records the text of every document printed through it.
 */
public class FakePrinter implements PrintService {

    private final List<String> jobs = new ArrayList<>();

    /** The text of each document that was printed, in print order. */
    public List<String> printedJobs() {
        return List.copyOf(jobs);
    }

    @Override
    public DocPrintJob createPrintJob() {
        return new FakePrintJob();
    }

    // One print job that captures the document instead of printing it.
    private class FakePrintJob implements DocPrintJob {

        @Override
        public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {
            try {
                Object data = doc.getPrintData();
                if (!(data instanceof String text)) {
                    throw new PrintException("FakePrinter only supports text documents");
                }
                jobs.add(text);
            } catch (PrintException e) {
                throw e;
            } catch (Exception e) {
                throw new PrintException("Could not read print data", e);
            }
        }

        @Override public PrintService getPrintService() { return FakePrinter.this; }
        @Override public PrintJobAttributeSet getAttributes() { return null; }
        @Override public void addPrintJobListener(PrintJobListener listener) { }
        @Override public void removePrintJobListener(PrintJobListener listener) { }
        @Override public void addPrintJobAttributeListener(PrintJobAttributeListener listener,
                                                           PrintJobAttributeSet attributes) { }
        @Override public void removePrintJobAttributeListener(PrintJobAttributeListener listener) { }
    }

    // The rest of the PrintService interface is capability reporting that
    // the code under test never asks a printer about.
    @Override public String getName() { return "Fake Receipt Printer"; }
    @Override public DocFlavor[] getSupportedDocFlavors() {
        return new DocFlavor[]{new DocFlavor("text/plain", "java.lang.String")};
    }
    @Override public boolean isDocFlavorSupported(DocFlavor flavor) { return true; }
    @Override public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) { }
    @Override public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) { }
    @Override public PrintServiceAttributeSet getAttributes() { return null; }
    @Override public <T extends javax.print.attribute.PrintServiceAttribute> T getAttribute(Class<T> category) { return null; }
    @Override public Class<?>[] getSupportedAttributeCategories() { return new Class[0]; }
    @Override public boolean isAttributeCategorySupported(Class<? extends Attribute> category) { return false; }
    @Override public Object getDefaultAttributeValue(Class<? extends Attribute> category) { return null; }
    @Override public Object getSupportedAttributeValues(Class<? extends Attribute> category,
                                                        DocFlavor flavor, AttributeSet constraints) { return null; }
    @Override public boolean isAttributeValueSupported(Attribute attribute,
                                                       DocFlavor flavor, AttributeSet constraints) { return false; }
    @Override public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) { return null; }
    @Override public javax.print.ServiceUIFactory getServiceUIFactory() { return null; }
}

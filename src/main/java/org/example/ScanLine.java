package org.example;

/**
 * One scanned sales line of a purchase occasion: quantity label, full
 * description, the line's own VAT-inclusive price and the running total
 * after this line was scanned (spec &sect;4.10).
 */
public class ScanLine {

    private final String quantity;
    private final String description;
    private final long lineTotalCents;
    private final long runningTotalCents;

    public ScanLine(String quantity, String description, long lineTotalCents,
                    long runningTotalCents) {
        this.quantity = quantity;
        this.description = description;
        this.lineTotalCents = lineTotalCents;
        this.runningTotalCents = runningTotalCents;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public long getLineTotalCents() {
        return lineTotalCents;
    }

    public long getRunningTotalCents() {
        return runningTotalCents;
    }
}

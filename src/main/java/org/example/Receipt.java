package org.example;

import java.util.List;

/**
 * The single receipt renderer of the system (spec &sect;4.15): store name,
 * one line per scanned article with quantity/weight and price, an optional
 * discount line and the total paid.
 */
public class Receipt {

    private final String storeName;
    private final List<ScanLine> lines;
    private final long discountCents;
    private final long totalPaidCents;

    public Receipt(String storeName, List<ScanLine> lines, long discountCents,
                   long totalPaidCents) {
        this.storeName = storeName;
        this.lines = List.copyOf(lines);
        this.discountCents = discountCents;
        this.totalPaidCents = totalPaidCents;
    }

    /**
     * Renders the receipt text.
     *
     * @return the multi-line receipt, ending with the thank-you line
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Store name: ").append(storeName).append("\n");
        for (ScanLine line : lines) {
            sb.append(line.getQuantity()).append('\t')
                    .append(line.getDescription()).append("\t\t")
                    .append(PriceFormatter.format(line.getLineTotalCents()))
                    .append('\n');
        }
        if (discountCents > 0) {
            sb.append("Discount -20%: ")
                    .append(PriceFormatter.format(-discountCents))
                    .append('\n');
        }
        sb.append("Total paid: ")
                .append(PriceFormatter.format(totalPaidCents))
                .append("\n");
        sb.append("Thank you for shopping at ").append(storeName).append("!");
        return sb.toString();
    }
}

package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accumulates scanned sale items into grouped sales lines with running
 * VAT-inclusive totals (spec &sect;4.10): identical unit articles merge
 * into one counted line, weighed entries keep their own line.
 */
public class ScanLedger {

    private final List<ScanLine> lines = new ArrayList<>();
    private final Map<String, Integer> unitLineIndexByName = new HashMap<>();
    private long totalCents;

    /**
     * Books one scanned sale item onto the ledger.
     *
     * @param scanned the concrete sale item from the barcode scanner
     */
    public void record(Item scanned) {
        totalCents += scanned.getInclusivePrice();
        if (scanned instanceof WeighedItem weighed) {
            addWeighedLine(weighed);
        } else {
            mergeUnitLine(scanned);
        }
    }

    /**
     * The sales lines recorded so far, in scan order.
     *
     * @return immutable list of lines with running totals
     */
    public List<ScanLine> getLines() {
        return List.copyOf(lines);
    }

    /**
     * The accumulated VAT-inclusive total, in cents.
     *
     * @return running total
     */
    public long getTotalCents() {
        return totalCents;
    }

    private void addWeighedLine(WeighedItem weighed) {
        lines.add(new ScanLine(weighed.getWeight() + "g",
                weighed.getFullDescription(), weighed.getInclusivePrice(),
                totalCents));
    }

    private void mergeUnitLine(Item scanned) {
        Integer index = unitLineIndexByName.get(scanned.getName());
        if (index == null) {
            lines.add(new ScanLine("1", scanned.getFullDescription(),
                    scanned.getInclusivePrice(), totalCents));
            unitLineIndexByName.put(scanned.getName(), lines.size() - 1);
        } else {
            ScanLine line = lines.get(index);
            int count = Integer.parseInt(line.getQuantity()) + 1;
            lines.set(index, new ScanLine(String.valueOf(count),
                    line.getDescription(),
                    line.getLineTotalCents() + scanned.getInclusivePrice(),
                    totalCents));
        }
    }
}

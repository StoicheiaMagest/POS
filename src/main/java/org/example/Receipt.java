package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Receipt {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;
    private final String storeName;
    private final PurchaseOccasion purchaseOccasion;
    private final String receiptText;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int LINE_WIDTH = 42;
    private static final char DECIMAL_SEPARATOR = ',';
    private static final String CUT_MARKER = "~";
    private static final String EQUALS_LINE = "=".repeat(LINE_WIDTH);
    private static final String DASH_LINE = "-".repeat(LINE_WIDTH);

    public Receipt(int receiptNumber, LocalDateTime receiptDate, String storeName, PurchaseOccasion purchaseOccasion) {
        if (receiptNumber < 1) {
            throw new IllegalArgumentException("Receipt number must be greater than 0.");
        }

        if (storeName == null) {
            throw new NullPointerException("Store name must not be null.");
        }

        if (storeName.isBlank()) {
            throw new IllegalArgumentException("Store name must not be blank.");
        }

        if (receiptDate == null) {
            throw new NullPointerException("Receipt date must not be null.");
        }

        if (purchaseOccasion == null) {
            throw new NullPointerException("Purchase occasion must not be null.");
        }

        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.storeName = storeName;
        this.purchaseOccasion = purchaseOccasion;
        // built once here, so the receipt stays the same if the occasion changes later
        this.receiptText = buildReceiptText();
    }

    public String getStoreName() {
        return storeName;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public PurchaseOccasion getPurchaseOccasion() {
        return purchaseOccasion;
    }

    public String getFormattedDate() {
        return DATE_FORMAT.format(receiptDate);
    }

    public String getReceiptText() {
        return receiptText;
    }

    private String buildReceiptText() {
        List<String> lines = new ArrayList<>();
        lines.add(EQUALS_LINE);
        lines.add(centered(storeName));
        lines.add(EQUALS_LINE);
        lines.add("Kvittonr:    " + receiptNumber);
        lines.add("Datum:       " + getFormattedDate());
        lines.add(DASH_LINE);

        for (Item item : purchaseOccasion.getItems()) {
            String name = item.getName() == null ? "" : item.getName();
            lines.add(row(name, formatAmount(item.getInclusivePrice())));
        }
        lines.add(DASH_LINE);

        addDiscountBlock(lines);

        lines.add(row("SUMMA", formatAmount(purchaseOccasion.getTotalPriceForPurchase())));
        if (purchaseOccasion.isPaid()) {
            lines.add(DASH_LINE);
            lines.add("BETALT");
        }
        lines.add(EQUALS_LINE);
        return String.join("\n", lines);
    }

    // The subtotal and the discounts, only when at least one discount is above zero.
    // The multi-item discounts come from an unordered map, so they are sorted by name.
    private void addDiscountBlock(List<String> lines) {
        Map<String, Long> multiItemDiscounts = new TreeMap<>();
        for (Map.Entry<String, Long> discount : purchaseOccasion.getDiscounts().entrySet()) {
            if (discount.getValue() > 0) {
                multiItemDiscounts.put(discount.getKey(), discount.getValue());
            }
        }
        long couponDiscount = purchaseOccasion.getCouponDiscountTotal();
        long seniorDiscount = purchaseOccasion.getSeniorDiscountAmount();

        if (multiItemDiscounts.isEmpty() && couponDiscount <= 0 && seniorDiscount <= 0) {
            return;
        }

        lines.add(row("Delsumma", formatAmount(purchaseOccasion.getSubtotal())));
        for (Map.Entry<String, Long> discount : multiItemDiscounts.entrySet()) {
            lines.add(row(discount.getKey(), formatAmount(-discount.getValue())));
        }
        if (couponDiscount > 0) {
            lines.add(row("Kuponger", formatAmount(-couponDiscount)));
        }
        if (seniorDiscount > 0) {
            lines.add(row("Seniorrabatt", formatAmount(-seniorDiscount)));
        }
        lines.add(DASH_LINE);
    }

    // A name longer than the line is printed as it is.
    private String centered(String text) {
        if (text.length() >= LINE_WIDTH) {
            return text;
        }
        return " ".repeat((LINE_WIDTH - text.length()) / 2) + text;
    }

    // Cents as text with a decimal separator, for example 1590 -> 15,90 and -5 -> -0,05.
    private String formatAmount(long cents) {
        String sign = cents < 0 ? "-" : "";
        long absoluteCents = Math.abs(cents);
        long whole = absoluteCents / 100;
        long fraction = absoluteCents % 100;
        return sign + whole + DECIMAL_SEPARATOR + (fraction < 10 ? "0" : "") + fraction;
    }

    // One line with the label to the left and the amount ending in the last column.
    // A label that does not fit is cut off and ends with the cut marker.
    private String row(String label, String amount) {
        int maxLabelLength = LINE_WIDTH - amount.length() - 1;
        if (label.length() > maxLabelLength) {
            label = label.substring(0, maxLabelLength - CUT_MARKER.length()) + CUT_MARKER;
        }
        int padding = LINE_WIDTH - label.length() - amount.length();
        return label + " ".repeat(padding) + amount;
    }

    @Override
    public String toString() {
        return "Receipt{receiptNumber=" + receiptNumber + ", receiptDate=" +
                receiptDate + ", storeName=" + storeName +
                ", purchaseOccasion=" + purchaseOccasion + "}";
    }
}

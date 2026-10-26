package org.example;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class Receipt {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;
    private final String storeName;
    private final PurchaseOccasion purchaseOccasion;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    // Prints the sale: header, item lines, discount lines (in priority order), total.
    public String print() {
        NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("sv-SE"));
        StringBuilder text = new StringBuilder();
        text.append(storeName).append('\n');
        text.append(getFormattedDate()).append("  kvitto ").append(receiptNumber).append('\n');
        for (Item item : purchaseOccasion.getCustomerBasket().getItems()) {
            text.append(item.getName());
            if (item instanceof WeighedItem weighed) {
                text.append("  ").append(weighed.getWeight()).append(" g");
            }
            text.append("  ").append(formatOre(format, item.getInclusivePrice())).append('\n');
        }
        for (Map.Entry<String, Long> line : purchaseOccasion.getDiscounts().entrySet()) {
            text.append(line.getKey()).append("  -")
                    .append(formatOre(format, line.getValue())).append('\n');
        }
        text.append("Totalt  ")
                .append(formatOre(format, purchaseOccasion.getTotalPrice()));
        return text.toString();
    }

    private String formatOre(NumberFormat format, long ore) {
        return format.format(ore / 100.0) + " SEK";
    }

    @Override
    public String toString() {
        return "Receipt{receiptNumber=" + receiptNumber + ", receiptDate=" +
                receiptDate + ", storeName=" + storeName +
                ", purchaseOccasion=" + purchaseOccasion + "}";
    }
}

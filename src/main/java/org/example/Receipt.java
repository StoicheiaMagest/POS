package org.example;

import java.time.LocalDateTime;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public String print() {
        long totalCents = purchaseOccasion.getBasket().getTotalPrice();
        return storeName + "\n"
                + "Receipt number: " + receiptNumber + "\n"
                + "Date: " + getFormattedDate() + "\n"
                + "Total: " + formatCents(totalCents) + " SEK";
    }

    private static String formatCents(long cents) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("sv-SE"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(cents / 100.0);
    }

    @Override
    public String toString() {
        return "Receipt{receiptNumber=" + receiptNumber + ", receiptDate=" +
                receiptDate + ", storeName=" + storeName +
                ", purchaseOccasion=" + purchaseOccasion + "}";
    }
}

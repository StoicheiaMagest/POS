package org.example;

import java.time.LocalDateTime;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Receipt {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;
    private final String storeName;
    private final PurchaseOccasion purchaseOccasion;
    private final char decimalSeparator;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Receipt(int receiptNumber, LocalDateTime receiptDate, String storeName,
                   PurchaseOccasion purchaseOccasion) {
        this(receiptNumber, receiptDate, storeName, purchaseOccasion, ',');
    }

    public Receipt(int receiptNumber, LocalDateTime receiptDate, String storeName,
                   PurchaseOccasion purchaseOccasion, char decimalSeparator) {
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
        this.decimalSeparator = decimalSeparator;
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
        Basket basket = purchaseOccasion.getBasket();
        StringBuilder sb = new StringBuilder();
        sb.append(storeName).append("\n")
                .append("Receipt number: ").append(receiptNumber).append("\n")
                .append("Date: ").append(getFormattedDate()).append("\n");
        for (Item item : basket.getItems()) {
            sb.append(item.getName()).append(" ");
            if (item instanceof WeighedItem weighed) {
                sb.append(formatDecimal(weighed.getWeight(), 3)).append(" kg x ")
                        .append(formatCents(weighed.getPricePerKg())).append(" = ");
            }
            sb.append(formatCents(item.getInclusivePrice())).append(" SEK\n");
        }
        sb.append("Total: ").append(formatCents(basket.getTotalPrice())).append(" SEK");
        return sb.toString();
    }

    private String formatCents(long cents) {
        return formatDecimal(cents, 2);
    }

    // Formats an integer holding `decimals` implied decimal places.
    private String formatDecimal(long value, int decimals) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("sv-SE"));
        symbols.setDecimalSeparator(decimalSeparator);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        formatter.setMinimumFractionDigits(decimals);
        formatter.setMaximumFractionDigits(decimals);
        return formatter.format(value / Math.pow(10, decimals));
    }

    @Override
    public String toString() {
        return "Receipt{receiptNumber=" + receiptNumber + ", receiptDate=" +
                receiptDate + ", storeName=" + storeName +
                ", purchaseOccasion=" + purchaseOccasion + "}";
    }
}

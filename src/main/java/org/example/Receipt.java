package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
        // Identical unit-priced items share a key and are printed as one line.
        Map<String, List<Item>> lines = new LinkedHashMap<>();
        int lineNumber = 0;
        for (Item item : basket.getItems()) {
            String key = item instanceof WeighedItem
                    ? "weighed-" + lineNumber++
                    : item.getArticleNumber() + "|" + item.getName() + "|" + item.getInclusivePrice();
            lines.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        for (List<Item> group : lines.values()) {
            Item item = group.get(0);
            sb.append(item.getName()).append(" ");
            if (item instanceof WeighedItem weighed) {
                sb.append(Money.formatDecimal(weighed.getWeight(), 3, decimalSeparator)).append(" kg x ")
                        .append(formatCents(weighed.getPricePerKg())).append(" = ");
            } else if (group.size() > 1) {
                sb.append(group.size()).append(" x ")
                        .append(formatCents(item.getInclusivePrice())).append(" = ");
            }
            sb.append(formatCents(group.stream().mapToLong(Item::getInclusivePrice).sum()))
                    .append(" SEK\n");
        }
        // Discounts, sorted by name so the order is stable.
        Map<String, Long> discounts = new TreeMap<>(new Discount().getAllDiscounts(basket.getItems()));
        long totalSaved = 0;
        for (Map.Entry<String, Long> discount : discounts.entrySet()) {
            sb.append(discount.getKey()).append(" -")
                    .append(formatCents(discount.getValue())).append(" SEK\n");
            totalSaved += discount.getValue();
        }
        // VAT per rate = inclusive price minus exclusive price, summed over the items.
        Map<Vat, Long> vatByRate = new EnumMap<>(Vat.class);
        for (Item item : basket.getItems()) {
            vatByRate.merge(item.getVatRate(), item.getInclusivePrice() - item.getPrice(), Long::sum);
        }
        vatByRate.forEach((vat, amount) -> sb.append("VAT ")
                .append(Money.format(vat.getBasisPoints(), decimalSeparator)).append("%: ")
                .append(formatCents(amount)).append(" SEK\n"));
        if (totalSaved > 0) {
            sb.append("You saved: ").append(formatCents(totalSaved)).append(" SEK\n");
        }
        sb.append("Total: ").append(formatCents(basket.getTotalPrice() - totalSaved)).append(" SEK");
        return sb.toString();
    }

    private String formatCents(long cents) {
        return Money.format(cents, decimalSeparator);
    }

    @Override
    public String toString() {
        return "Receipt{receiptNumber=" + receiptNumber + ", receiptDate=" +
                receiptDate + ", storeName=" + storeName +
                ", purchaseOccasion=" + purchaseOccasion + "}";
    }
}

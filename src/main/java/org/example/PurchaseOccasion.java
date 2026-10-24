package org.example;
import java.text.NumberFormat;
import java.util.Locale;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private Basket customerBasket;
    private Customer customer;
    private Discount[] discounts;
    private long totalPrice; // cents



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Account storeBankAccount,
            Discount... discounts
    ) {
        this.customerBasket = basket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discounts = discounts;
        this.totalPrice = basket.getTotalPrice();
    }

    public void applyDiscount(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        // Add 50 before division to round to the nearest cent. Send new total back up.
        totalPrice = (totalPrice * (100L - discountPercent) + 50) / 100;
    }

    public String printReceipt() {
        NumberFormat formatter = NumberFormat.getNumberInstance(
                Locale.forLanguageTag("sv-SE")
        );
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return "Kvitto\n"
                + "Antal varor: " + customerBasket.getItems().size() + "\n"
                + "Totalt: " + formatter.format(totalPrice / 100.0) + " SEK";
    }

    public long getTotalPriceForPurchace() {
        return totalPrice;
    }

}


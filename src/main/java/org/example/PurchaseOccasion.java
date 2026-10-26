package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private Basket customerBasket;
    private Customer customer;
    private Map<String, Long> discounts;;
    private long totalPrice; // cents
    Discount discount = new Discount();



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Account storeBankAccount,
            HashMap<String, Long> discounts
    ) {
        this.customerBasket = basket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discounts = discount.getAllDiscounts(customerBasket.getItems());
        this.totalPrice = basket.getTotalPrice();
    }

    public void applyDiscount(Discount discounts) {
        for(long dis : discounts.getValue()) {
            if (dis < 0 || dis > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            // Add 50 before division to round to the nearest cent. Send new total back up.
            totalPrice = (totalPrice * (100L - dis) + 50) / 100;
        }
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

    public boolean getMembershipStatus() {
        return customer.getMembershipStatus();
    }

    public boolean getSeniorStatus() {
        return customer.getSeniorStatus();
    }

}


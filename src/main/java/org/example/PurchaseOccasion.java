package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private Basket customerBasket;
    private Customer customer;
    private HashMap<String, Long> discounts = new HashMap<>();
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

    public void applyDiscount(discounts) {
        for(long discount : discounts.getValue()) {
            if (discount < 0 || discount > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            // Add 50 before division to round to the nearest cent. Send new total back up.
            totalPrice = (totalPrice * (100L - discount) + 50) / 100;
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

}


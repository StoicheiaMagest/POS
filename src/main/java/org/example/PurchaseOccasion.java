package org.example;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private Basket customerBasket;
    private Customer customer;
    private Map<String, Long> discounts;
    private long totalPrice; // cents
    DiscountHandler discountHandler = new DiscountHandler();



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Account storeBankAccount 
    ) {
        this.customerBasket = basket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discounts = discountHandler.getAllDiscounts(customerBasket.getItems());
        this.totalPrice = basket.getTotalPrice();
    }

    public void applyDiscount() {
        for(Map.Entry<String, Long> discount : discounts.entrySet()) {
            if (discount.getValue() < 0 || discount.getValue() > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            totalPrice -= discount.getValue();
        }
    }

    public void applySeniorDiscount() {
        if(!customer.getSeniorStatus()) {
            throw new IllegalArgumentException("Error, not a senior");
        }
        // Add 50 before division to round to the nearest cent. Send new total back up.
        totalPrice = (totalPrice * (100L - DiscountHandler.getSeniorDiscount()) + 50) / 100;
    }

    /* 
    public void useCupon() {
        ArrayList<Coupon>() coupons = customer.getCoupons();
        for (Coupon coupon : coupons) {
            totalPrice = Math.max(0, (totalPrice - coupon));
        }
    }
    */

    public void payPurchase() {
        //customer.getCard();
        //totalPrice och nolla den för att "betala"
    }

    public String printReceipt() {
        NumberFormat formatter = NumberFormat.getNumberInstance(
                Locale.forLanguageTag("sv-SE")
        );
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        //printa även hela discountsmapen

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


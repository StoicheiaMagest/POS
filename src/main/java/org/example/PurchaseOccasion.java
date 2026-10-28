package org.example;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Account storeBankAccount; //behövs denna?
    private Basket customerBasket;
    private Customer customer;
    private Map<String, Long> discounts;
    private long totalPrice; // cents
    private boolean usedDiscount;
    DiscountHandler discountHandler = new DiscountHandler();



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Account storeBankAccount 
    ) {
        this.customerBasket = basket;
        if(customer == null) {
            throw new IllegalArgumentException("Customer can't be null!");
        }
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discounts = discountHandler.getAllDiscounts(customerBasket.getItems());
        this.totalPrice = basket.getTotalPrice();
    }

    public void applyDiscount() {
        if(customerBasket.getItems() == null || customerBasket.getItems().isEmpty()) {
            throw new IllegalArgumentException("Basket can't be empty");
        }

        for(Map.Entry<String, Long> discount : discounts.entrySet()) {
            if (discount.getValue() < 0 || discount.getValue() > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            totalPrice -= discount.getValue();
        }
        usedDiscount = true;
    }

    public void applySeniorDiscount() {
        if(!customer.getSeniorStatus()) {
            throw new IllegalArgumentException("Error, not a senior");
        }
        totalPrice = Math.round(totalPrice * (100 - DiscountHandler.getSeniorDiscount()));
        usedDiscount = true;
    }

    public void useCoupon() {
        /*ArrayList<Coupon>() coupons = customer.getCoupons();
        for (Coupon coupon : coupons) {
            totalPrice = Math.max(0, (totalPrice - coupon));
        }*/
    }

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

        StringBuilder sb = new StringBuilder();
        if(usedDiscount) {
            for(Map.Entry<String, Long> discount : discounts.entrySet()) {
                sb.append(discount.getKey());
                sb.append("\t");
                sb.append("- ");
                sb.append(discount.getValue() / 100.0);
                sb.append(" kr");
            }
        }
        sb.append("None");
        
        //Ändra till engelska?
        return "Kvitto\n"
                + "Antal varor: " + customerBasket.getItems().size() + "\n"
                + "Rabatter: " + sb + "\n"
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


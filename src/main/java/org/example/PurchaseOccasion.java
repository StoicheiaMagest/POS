package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Store store;
    private Basket customerBasket;
    private Customer customer;
    private long totalPrice; // cents
    private boolean usedDiscount;
    private Map<String, Long> discounts = new HashMap<>();



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Store store
    ) {
        this.customerBasket = basket;
        if(customer == null) {
            throw new IllegalArgumentException("Customer can't be null!");
        }
        this.customer = customer;
        this.store = store;
        this.totalPrice = basket.getTotalPrice();
    }

    public Store getStore() {
        return store;
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
        //kuponger får endast användas av medlemmar, icke medlemmar får ha kumponger med inte använda
        /*ArrayList<Coupon>() coupons = customer.getCoupons();
        for (Coupon coupon : coupons) {
            totalPrice = Math.max(0, (totalPrice - coupon));
        }*/
    }

    public void payPurchase() {
        //customer.getCard();
        //totalPrice och nolla den för att "betala"
    }
//
//    public String printReceipt() {
//        NumberFormat formatter = NumberFormat.getNumberInstance(
//                Locale.forLanguageTag("sv-SE")
//        );
//        formatter.setMinimumFractionDigits(2);
//        formatter.setMaximumFractionDigits(2);
//
//        StringBuilder sb = new StringBuilder();
//        if(usedDiscount) {
//            for(Map.Entry<String, Long> discount : discounts.entrySet()) {
//                sb.append(discount.getKey());
//                sb.append("\t");
//                sb.append("- ");
//                sb.append(discount.getValue() / 100.0);
//                sb.append(" kr");
//            }
//        }
//        sb.append("None");
//
//        //Ändra till engelska?
//        return "Kvitto\n"
//                + "Antal varor: " + customerBasket.getItems().size() + "\n"
//                + "Rabatter: " + sb + "\n"
//                + "Totalt: " + formatter.format(totalPrice / 100.0) + " SEK";
//    }

    public long getTotalPriceForPurchase() {
        return totalPrice;
    }

    public boolean getMembershipStatus() {
        return customer.getMembershipStatus();
    }

    public boolean getSeniorStatus() {
        return customer.getSeniorStatus();
    }

}


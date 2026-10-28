package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
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

    /*
     * Applies all discounts in the decision table's priority order:
     * 1. flertartikelsrabatt, 2. membership check, 3. coupons, 4. senior discount.
     */
    public void applyDiscounts() {
        applyMultiItemDiscounts();
        applyCouponsIfMember();
        applySeniorDiscountIfMemberAndSenior();
        usedDiscount = true;
    }

    // Step 1: the store's multi-item discounts, these apply to everyone.
    // The DiscountHandler is stateful, so getAllDiscounts is called exactly once.
    private void applyMultiItemDiscounts() {
        discounts = store.getDiscountHandler().getAllDiscounts(customerBasket.getItems());
        for (Map.Entry<String, Long> discount : discounts.entrySet()) {
            totalPrice -= discount.getValue();
        }
    }

    // Steps 2 and 3: only members may use coupons, as many valid ones as they want.
    private void applyCouponsIfMember() {
        if (!customer.getMembershipStatus()) {
            return;
        }
        LinkedList<Coupon> couponsToUse = new LinkedList<>(customer.getCoupons());
        for (Coupon coupon : couponsToUse) {
            useCouponIfValid(coupon);
        }
    }

    // Expired coupons are skipped and stay with the customer.
    private void useCouponIfValid(Coupon coupon) {
        if (coupon.isExpired()) {
            return;
        }
        totalPrice -= customer.useCouponMethod(coupon);
        if (totalPrice < 0) {
            totalPrice = 0;
        }
    }

    // Step 4: senior discount last, on whatever is left of the total,
    // only for members who are also seniors.
    private void applySeniorDiscountIfMemberAndSenior() {
        if (!customer.getMembershipStatus() || !customer.getSeniorStatus()) {
            return;
        }
        long seniorOff = Math.round(totalPrice * DiscountHandler.getSeniorDiscount() / 100.0);
        totalPrice -= seniorOff;
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


package org.example;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private DiscountHandler discountHandler;
    private Basket customerBasket;
    private Customer customer;
    private long totalPrice; // cents
    private boolean usedDiscount;
    private boolean discountsApplied;
    private boolean paid;
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
        this.storeBankAccount = store.getStoreBankAccount();
        this.discountHandler = store.getDiscountHandler();
        this.totalPrice = basket.getTotalPrice();
    }

    /*
     * Applies all discounts in the decision table's priority order:
     * 1. flertartikelsrabatt, 2. membership check, 3. coupons, 4. senior discount.
     * Can only be done once per occasion, a second call would subtract
     * every discount a second time.
     */
    public void applyDiscounts() {
        if (discountsApplied) {
            throw new IllegalStateException("Discounts have already been applied");
        }
        applyMultiItemDiscounts();
        applyCouponsIfMember();
        applySeniorDiscountIfMemberAndSenior();
        usedDiscount = true;
        discountsApplied = true;
    }

    // Step 1: the store's multi-item discounts, these apply to everyone.
    // The DiscountHandler is stateful, so getAllDiscounts is called exactly once.
    private void applyMultiItemDiscounts() {
        discounts = discountHandler.getAllDiscounts(customerBasket.getItems());
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

    /*
     * Pays the total price from the customer's card into the store's bank
     * account. Returns false when the customer has no card or the transfer
     * fails, for example when the card account does not cover the total.
     * Once paid, the occasion can never be paid again.
     */
    public boolean payPurchase() {
        if (paid) {
            throw new IllegalStateException("This purchase has already been paid");
        }
        Card card = customer.getCard();
        if (card == null) {
            return false;
        }
        boolean transferDone = card.pay(totalPrice, storeBankAccount);
        if (transferDone) {
            paid = true;
        }
        return transferDone;
    }

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


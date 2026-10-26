package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Basket customerBasket;
    private Customer customer;
    private Account storeBankAccount;
    private Discount discount = new Discount();
    private Coupon coupon; // the customer's first available coupon, redeemed on payment
    private boolean paid;
    //private long totalPrice; // cents

    public PurchaseOccasion(Basket customerBasket, Customer customer,
            Account storeBankAccount, Discount discount
    ) {
        this.customerBasket = customerBasket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discount = discount;
        this.coupon = customer != null && customer.getMembershipStatus()
                ? customer.getFirstAvailableCoupon() : null;
    }

    public Basket getCustomerBasket() {
        return customerBasket;
    }

    public Customer getCustomer() {
        return customer;
    }

    public long getSubtotal(){
        return customerBasket.getTotalPrice();
    }

    public Map<String, Long> getDiscounts(){
        return discount.getAllDiscounts(customerBasket, customer, coupon);
    }

    public long getTotalPrice() {
        return Math.max(0, getSubtotal() - Discount.totalOff(getDiscounts())); // never negative
    }

    public boolean pay(Card card) {
        if (paid || !card.pay(getTotalPrice(), storeBankAccount)) {
            return false;
        }
        paid = true;
        redeemCouponIfUsed();
        return true;
    }

    // A coupon is consumable: it is only used up when the payment succeeded.
    private void redeemCouponIfUsed() {
        if (coupon != null && getDiscounts().containsKey(Discount.couponKey(coupon))) {
            coupon.redeem();
        }
    }

    public long getTotalPriceForPurchace() {
        return getTotalPrice();
    }

}


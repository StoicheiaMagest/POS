package org.example;

// A consumable discount coupon: usable once, then redeemed. All money in ore (incl. VAT).
public class Coupon {

    public enum Type { PERCENT, FIXED }

    private final String name;
    private final Type type;
    private final long value;       // percent (1-100) or amount in ore
    private final long minPurchase; // minimum purchase in ore (0 = no threshold)
    private boolean redeemed;

    private Coupon(String name, Type type, long value, long minPurchase) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Coupon name must not be blank.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Coupon value must be positive.");
        }
        if (type == Type.PERCENT && value > 100) {
            throw new IllegalArgumentException("Coupon percent must not exceed 100.");
        }
        if (minPurchase < 0) {
            throw new IllegalArgumentException("Minimum purchase must not be negative.");
        }
        this.name = name;
        this.type = type;
        this.value = value;
        this.minPurchase = minPurchase;
    }

    // Creates a consumable percent coupon, e.g. Coupon.percentCoupon("10 %", 10, 0).
    public static Coupon percentCoupon(String name, int percent, long minPurchaseOre) {
        return new Coupon(name, Type.PERCENT, percent, minPurchaseOre);
    }

    // Creates a consumable fixed-amount coupon with a threshold, e.g. if (p >= 20000) p -= 2000.
    public static Coupon fixedCoupon(String name, long amountOre, long minPurchaseOre) {
        return new Coupon(name, Type.FIXED, amountOre, minPurchaseOre);
    }

    // Ore off the given price (after prior discounts). 0 if not usable.
    public long getDiscount(long price) {
        if (redeemed || price < minPurchase) {
            return 0;
        }
        if (type == Type.PERCENT) {
            return Math.min(price, (price * value + 50) / 100); // round half-up
        }
        return Math.min(price, value);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public long getMinPurchase() {
        return minPurchase;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    // Marks the coupon as used. Called by PurchaseOccasion after successful payment.
    void redeem() {
        redeemed = true;
    }
}

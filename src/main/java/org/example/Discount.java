package org.example;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Discount priority (decision table):
// 1) item discounts: 3 for 2 for everyone, 2 kr off for members only
// 2) membership gate - non-members stop here, whether they are senior or not
// 3) coupon on the remaining price
// 4) senior percent, always last on top of everything
public class Discount {
    private static final int SENIOR_PERCENT = 10;
    private static final long TWO_KR_OFF_AMOUNT = 200; // öre

    // description -> öre off (incl. VAT). Built fresh on every call.
    public Map<String, Long> getAllDiscounts(Basket basket, Customer customer) {
        return getAllDiscounts(basket, customer, null);
    }

    public Map<String, Long> getAllDiscounts(Basket basket, Customer customer, Coupon coupon) {
        // LinkedHashMap keeps the order we put things in, so the receipt shows
        // the discounts in priority order (a plain HashMap would not).
        Map<String, Long> discounts = new LinkedHashMap<>();
        boolean member = customer != null && customer.getMembershipStatus();
        addItemDiscounts(basket, discounts, member);

        // non-members only get 3 for 2
        if (!member) {
            return discounts;
        }

        long price = basket.getTotalPrice() - totalOff(discounts);

        // use the given coupon, or the customer's first unused one
        Coupon chosen = coupon;
        if (chosen == null) {
            chosen = customer.getFirstAvailableCoupon();
        }
        if (chosen != null) {
            long off = chosen.getDiscount(price);
            addOff(discounts, couponKey(chosen), off);
            price = price - off;
        }

        // seniors always get 10 % of what is left, rounded half-up
        if (customer.getSeniorStatus()) {
            long off = (price * SENIOR_PERCENT + 50) / 100;
            addOff(discounts, "Senior " + SENIOR_PERCENT + "%", off);
        }

        return discounts;
    }

    // Adds a discount line only when it is positive.
    private void addOff(Map<String, Long> discounts, String description, long ore) {
        if (ore > 0) {
            discounts.put(description, ore);
        }
    }

    private void addItemDiscounts(Basket basket, Map<String, Long> discounts, boolean member) {
        Map<Integer, List<Item>> byArticle = itemsByArticle(basket);
        for (List<Item> group : byArticle.values()) {
            Item first = group.get(0);
            DiscountType type = first.getDiscountType();
            if (type == DiscountType.THREE_FOR_TWO && group.size() >= 3) {
                // every third item is free
                addOff(discounts, "3 for 2 " + first.getName(),
                        (group.size() / 3) * first.getInclusivePrice());
            } else if (type == DiscountType.TWO_KR_OFF && member) {
                addOff(discounts, "2 kr off on " + first.getName(), twoKrOff(group));
            }
        }
    }

    // 2 kr off each item, but never more than the item costs.
    private long twoKrOff(List<Item> group) {
        long off = 0;
        for (Item item : group) {
            off = off + Math.min(TWO_KR_OFF_AMOUNT, item.getInclusivePrice());
        }
        return off;
    }

    // Items grouped by article number, keeping the scan order.
    private Map<Integer, List<Item>> itemsByArticle(Basket basket) {
        Map<Integer, List<Item>> byArticle = new LinkedHashMap<>();
        for (Item item : basket.getItems()) {
            int number = item.getArticleNumber();
            if (!byArticle.containsKey(number)) {
                byArticle.put(number, new ArrayList<Item>());
            }
            byArticle.get(number).add(item);
        }
        return byArticle;
    }

    // Sum of all discount lines in öre.
    public static long totalOff(Map<String, Long> discounts) {
        long total = 0;
        for (long off : discounts.values()) {
            total = total + off;
        }
        return total;
    }

    public static String couponKey(Coupon coupon) {
        return "Coupon " + coupon.getName();
    }

    public int getSeniorDiscountProcent() {
        return SENIOR_PERCENT;
    }
}

package org.example;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class Discount {
    private static final int SENIOR_PERCENT = 10;
    // description -> öre off (incl. VAT). Built fresh on every call.
    public Map<String, Long> getAllDiscounts(Basket basket, Customer customer) {
        Map<String, Long> discounts = new LinkedHashMap<>();
        Map<Integer, List<Item>> byArticle = new LinkedHashMap<>();
        for (Item item : basket.getItems()) {
            byArticle.computeIfAbsent(item.getArticleNumber(), k -> new ArrayList<>()).add(item);
        }
        for (List<Item> group : byArticle.values()) {
            Item first = group.get(0);
            String type = getDiscountType(first);
            if ("3for2".equals(type) && group.size() >= 3) {
                discounts.put("3 for 2 " + first.getName(),
                        (group.size() / 3) * first.getInclusivePrice());
            } else if ("2krOff".equals(type)) {
                discounts.put("2 kr off on " + first.getName(), 200L * group.size());
            }
        }
        if (customer != null && customer.getSeniorStatus()) {
            long itemOff = discounts.values().stream().mapToLong(Long::longValue).sum();
            long after = basket.getTotalPrice() - itemOff;
            discounts.put("Senior " + SENIOR_PERCENT + "%", (after * SENIOR_PERCENT + 50) / 100);
        }
        return discounts;
    }
    public String getDiscountType(Item item) {
        switch (item.getArticleNumber()) {
            case 111: return "3for2";   // milk
            case 222: return "2krOff";  // bread
            default:  return null;
        }
    }
    public int getSeniorDiscountProcent() {
        return SENIOR_PERCENT;
    }
}
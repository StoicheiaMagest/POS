package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/*
    coupon, one per purchace
    consumable
    


*/


public class Discount {

    //takes list of all items and checks each item if there is any that have a discount
    public Map<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        Map<String, Long> discountsMap = new HashMap<>();
        for (DiscountLine line : getDiscountLines(orderListen)) {
            discountsMap.merge(line.name(), line.amount(), Long::sum);
        }
        return discountsMap;
    }

    // Same discounts as getAllDiscounts, but each one also carries the VAT rate
    // of its article. 2 kr off lines come first, then 3 for 2 lines.
    public List<DiscountLine> getDiscountLines(List<Item> items){
        Map<String, DiscountLine> breadLines = new LinkedHashMap<>();
        Map<Integer, List<Item>> threeForTwoItems = new LinkedHashMap<>();
        for (Item item : items) {
            if("2krOff".equals(getDiscountType(item))){
                // 2 kr = 200 cents, per item
                breadLines.merge("2 kr off on " + item.getName(),
                        new DiscountLine("2 kr off on " + item.getName(), 200L, item.getVatRate()),
                        (a, b) -> new DiscountLine(a.name(), a.amount() + b.amount(), a.vat()));
            }
            if("3for2".equals(getDiscountType(item))){
                threeForTwoItems.computeIfAbsent(item.getArticleNumber(), k -> new ArrayList<>()).add(item);
            }
        }
        List<DiscountLine> lines = new ArrayList<>(breadLines.values());
        for (List<Item> group : threeForTwoItems.values()) {
            long saving = check3for2Discount(group);
            if (saving > 0) {
                Item first = group.get(0);
                lines.add(new DiscountLine("3 for 2 " + first.getName(), saving, first.getVatRate()));
            }
        }
        return lines;
    }

    public String getDiscountType(Item item){
        switch (item.getArticleNumber()) {
            case 111: //milk
                return "3for2";
            case 222: //bread
                return "2krOff";
            default:
                return null;
        }
    }

    // Every full group of 3 identical articles gets its cheapest item free.
    // Returns the saving in cents.
    public long check3for2Discount(List<Item> sameArticle){
        List<Long> prices = new ArrayList<>();
        for (Item item : sameArticle) {
            prices.add(item.getInclusivePrice());
        }
        prices.sort(Comparator.naturalOrder());
        long saving = 0;
        for (int i = 0; i < prices.size() / 3; i++) {
            saving += prices.get(i);
        }
        return saving;
    }
}

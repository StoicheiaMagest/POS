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
        Map<Integer, List<Item>> threeForTwoItems = new LinkedHashMap<>();
        for (Item item : orderListen) {
            if("2krOff".equals(getDiscountType(item))){
                // 2 kr = 200 cents, per item
                discountsMap.merge("2 kr off on "+item.getName(), 200L, Long::sum);
            }
            if("3for2".equals(getDiscountType(item))){
                threeForTwoItems.computeIfAbsent(item.getArticleNumber(), k -> new ArrayList<>()).add(item);
            }
        }
        for (List<Item> group : threeForTwoItems.values()) {
            long saving = check3for2Discount(group);
            if (saving > 0) {
                discountsMap.put("3 for 2 " + group.get(0).getName(), saving);
            }
        }
        return discountsMap;
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

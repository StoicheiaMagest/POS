package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/*
    coupon, one per purchace
    consumable
    


*/


public class Discount {
    //Map of discount name and value
    HashMap<String, Long> discountsMap = new HashMap<>();
    //Ex. [ 3 for 2 milk, 10], [3 for 2 juice, 5] 
    // [ milk, 3 for 2]
    ArrayList<Item> orderList = new ArrayList<>();

    //takes list of all items and checks each item if there is any that have a discount
    public HashMap<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        for (Item item : orderListen) {
            if(getDiscountType(item).equals("2krOff")){
                //if(discountsMap)
                discountsMap.put("2 kr off on "+item.getName(), 2l);
            } 
            if(getDiscountType(item).equals("3for2")){
                check3for2Discount(orderListen);
                //discountsMap.put("3 for 2 "+item.getName(), (long)item.getPrice());
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

    //
    public void check3for2Discount(ArrayList<Item> itemList){
        ArrayList<Long> listof3for2 = new ArrayList<>();
        for (Item item : itemList) {
            if(getDiscountType(item).equals("2p")){
                listof3for2.add((long)item.getArticleNumber());
            }   
        }

        /*
        Map<Long, Long> antal = new HashMap<>();
        for (Item item : itemList) {
            antal.merge((long)item.getArticleNumber(), 1l, Long::sum);
        }

        for (Map.Entry<Long, Long> e : antal.entrySet()) {
            Long attTaBort = (e.getValue() / 3) * 3;   // heltalsdivision
            Iterator<Item> it = orderList.iterator();
            while (it.hasNext() && attTaBort > 0) {
                if (it.next().getArticleNumber() == e.getKey()) {
                    it.remove();
                    attTaBort--;
                }
            }
        } */
    }
}

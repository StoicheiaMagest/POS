package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Discount {
    //Map of discount name and value
    Map<String, Long> discountsMap = new HashMap<>();
    //Ex. [3 for 2 milk, 10] , [3 for 2 juice, 5] 

    ArrayList<Item> orderList = new ArrayList<>();

    //takes list of all items and checks each item if there is any that have a discount
    public Map<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        for (Item item : orderListen) {
            if(getDiscountType(item).equals("2p")){
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
                return "2p";
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
    }



}

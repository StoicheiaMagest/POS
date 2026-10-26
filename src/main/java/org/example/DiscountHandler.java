package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class DiscountHandler {
    //Map of discount name and value
    Map<String, Long> discountsMap = new HashMap<>();
    //Ex. [ 3 for 2 milk, 10], [3 for 2 juice, 5] 
    // [ milk, 3 for 2]
    ArrayList<Item> orderList = new ArrayList<>();

    Map<Item, Integer> map3For2Counter = new HashMap<>();

    //takes list of all items and checks each item if there is any that have a discount
    public Map<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        for (Item item : orderListen) {
              switch (item.getDiscountType()) {  
                case "3FOR2": //implementera så att den räknar antal produkter korrekt
                    check3for2Discount(item);
                    break;
                case "2KR_OFF": 
                    discountsMap.put(item.getDiscountType(), (long) 2);
                    break;
                default:
                    break;
            }
        }   
        return discountsMap;
    }

    public Map<String, Long> getDiscountMap(){
        return discountsMap;
    }

    //temp
    public static long getSeniorDiscount(){
        return 10;
    }

    public void check3for2Discount(Item item){
        if(map3For2Counter.containsKey(item)){
            if(map3For2Counter.get(item)==2){
                map3For2Counter.remove(item);
                discountsMap.put(item.getDiscountType(),(long)item.getPrice());
            }
            else{
                map3For2Counter.replace(item, 2);
            }
        }
        else{
            map3For2Counter.put(item, 1);
        }
    }
}

package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscountHandler {
    
    //Map of discount name and value
    Map<String, Long> discountsMap = new HashMap<>();
    Map<Item, Integer> item3For2Counter = new HashMap<>();

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

    public Map<Item, Integer> getItemCounterMap(){
        return item3For2Counter;
    }

    //temp
    public long getSeniorDiscount(){
        return 10;
    }

    public void check3for2Discount(Item item){
        if(item3For2Counter.containsKey(item)){
            if(item3For2Counter.get(item)==2){
                item3For2Counter.remove(item);
                discountsMap.put(item.getDiscountType(),(long)item.getPrice());
            }
            else{
                item3For2Counter.replace(item, 2);
            }
        }
        else{
            item3For2Counter.put(item, 1);
        }
    }

}

package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscountHandler {
    
    //Map of discount name and value
    private Map<String, Long> discountsMap = new HashMap<>();
    private Map<Item, Long> item3For2Counter = new HashMap<>();
    private static final long SENIOR_DISCOUNT_PROCENTAGE= 10;

    //takes list of all items and checks each item if there is any that have a discount
    public Map<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        for (Item item : orderListen) {
            switch (item.getDiscountType()) {  
                case "3FOR2": //implementera så att den räknar antal produkter korrekt
                    check3for2Discount(item);
                    break;
                case "2KR_OFF": 
                    discountsMap.put(item.getDiscountType() +" on "+item.getName(), (long) 2);
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

    public Map<Item, Long> getItemCounterMap(){
        return item3For2Counter;
    }

    //temp
    public static long getSeniorDiscount(){
        return SENIOR_DISCOUNT_PROCENTAGE;
    }

    public void check3for2Discount(Item item){
        if(item3For2Counter.containsKey(item)){
            if(item3For2Counter.get(item)==2){
                item3For2Counter.remove(item);
                discountsMap.put(item.getDiscountType() +" on "+item.getName(),item.getPrice());
            }
            else{
                item3For2Counter.replace(item, 2l);
            }
        }
        else{
            item3For2Counter.put(item, 1l);
        }
    }

}

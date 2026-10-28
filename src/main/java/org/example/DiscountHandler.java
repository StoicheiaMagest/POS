package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscountHandler {
    
    //Map of discount name and value
    private Map<String, Long> discountsMap = new HashMap<>();
    private Map<Item, Long> itemCounter = new HashMap<>();
    private static final long SENIOR_DISCOUNT_PROCENTAGE= 10;

    //takes list of all items and checks each item if there is any that have a discount
    public Map<String, Long>  getAllDiscounts(ArrayList<Item> orderListen){
        for (Item item : orderListen) {
            switch (item.getDiscountType()) {  
                case "3FOR2": //implementera så att den räknar antal produkter korrekt
                    checkMultiItemDiscount(item, 3);
                    break;
                case "4FOR3":
                    checkMultiItemDiscount(item, 4);
                    break;
                case "5FOR4":
                    checkMultiItemDiscount(item, 5);
                    break;
                /*ase "2KR_OFF": 
                    discountsMap.put(item.getDiscountType() +" on "+item.getName(), (long) 2);
                    break;*/
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
        return itemCounter;
    }

    //temp
    public static long getSeniorDiscount(){
        return SENIOR_DISCOUNT_PROCENTAGE;
    }

    protected  void checkMultiItemDiscount(Item item, long neededNumberOfItem){
        if(itemCounter.containsKey(item)){
            if(itemCounter.get(item)==neededNumberOfItem-1){
                itemCounter.remove(item);
                discountsMap.put(item.getDiscountType() +" on "+item.getName(),item.getPrice());
            }
            else{
                itemCounter.replace(item, itemCounter.get(item)+1);
            }
        }
        else{
            itemCounter.put(item, 1l);
        }
    }



    /*public void check3for2Discount(Item item){
        if(itemCounter.containsKey(item)){
            if(itemCounter.get(item)==2){
                itemCounter.remove(item);
                discountsMap.put(item.getDiscountType() +" on "+item.getName(),item.getPrice());
            }
            else{
                itemCounter.replace(item, itemCounter.get(item)+1);
            }
        }
        else{
            itemCounter.put(item, 1l);
        }
    }*/

}

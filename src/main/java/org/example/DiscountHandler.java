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
            //items without a discount type have null here, switch(null) would crash
            String discountType = item.getDiscountType();
            if (discountType == null) {
                continue;
            }

            switch (discountType) {  
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

    //Adds a discount amount to the map. The same article can complete several
    //discount groups (for example six milks give two 3-for-2 discounts), so an
    //already existing entry is increased instead of overwritten.
    protected void addDiscount(String key, long amount){
        long alreadyFound = 0;
        if(discountsMap.containsKey(key)){
            alreadyFound = discountsMap.get(key);
        }
        discountsMap.put(key, alreadyFound + amount);
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
                addDiscount(item.getDiscountType() +" on "+item.getName(), item.getPrice());
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

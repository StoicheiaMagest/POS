package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Discount {
    //Map of discount name and value
    Map<String, Integer> discountsMap = new HashMap<>();
    //Ex. [3 for 2 milk, 10] , [3 for 2 juice, 5] 

    ArrayList<Item> orderList = new ArrayList<>();

    public void checkForDiscounts(ArrayList<Item> orderList){
        this.orderList = orderList;
    }

    //
    public void add20krDiscount(){

    }

    //
    public void check3for2Discount(ArrayList<Item> orderList){
        for (Item i : orderList){
            
        }
    }

    //
    public int getSeniorDiscountProcent(){
        return 10;
    }

    //sends back the sum of all discounts
    /*
    public int totalDiscountSum(){

        int sum = discountsMap.get(discountsMap);

        return sum;
    }*/

}

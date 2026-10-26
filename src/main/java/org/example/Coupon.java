package org.example;

import java.util.ArrayList;

public class Coupon {

    /*
    coupon, one per purchace
    consumable
    each customer has a array of coupons

    everywhere there is a "Discount[] discount" should instead be this coupon
    */

    String name;
    int reduction;
    //dateTime experationDate;

    public Coupon(String name, int reduciton){
        this.name = name;
        this.reduction = reduciton;
    }


}

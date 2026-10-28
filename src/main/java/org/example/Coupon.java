package org.example;

public class Coupon {

//OBS LÅG PRIORITET 
//KANSKE INTE ÖVERLEVER 


    /*
    coupon, one per purchace
    consumable
    each customer has a array of coupons

    everywhere there is a "Discount[] discount" should instead be this coupon
    */



    private final String name;
    private final long reduction;
    //dateTime experationDate;

    public Coupon(String name, long reduction){
        //throw exeption if null or negetive value
        this.name = name;
        this.reduction = reduction;
    }

    public String getName(){
        return name;
    }
    public long getReduction(){
        return reduction;
    }

}

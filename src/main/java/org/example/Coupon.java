package org.example;

import java.time.LocalDateTime;

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
    private final LocalDateTime expiryDate;

    public Coupon(String name, long reduction, LocalDateTime expiryDate){
        //throw exeption if null or negetive value
        this.name = name;
        this.reduction = reduction;
        this.expiryDate = expiryDate;
    }

    /**
     * A coupon is expired when its expiry date is before now.
     */
    public boolean isExpired(){
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public String getName(){
        return name;
    }

    public long getReduction(){
        return reduction;
    }

}

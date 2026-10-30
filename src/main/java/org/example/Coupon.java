package org.example;

import java.time.LocalDateTime;

public class Coupon {
    /*
    coupon, one per purchace
    consumable
    each customer has a array of coupons
    */
    private final String name;
    private final long reduction;
    private final LocalDateTime expiryDate;

    public Coupon(String name, long reduction, LocalDateTime expiryDate){
        //throw exeption if null or negetive value
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Invalid coupon name");
        }        
        //if name contains % or "procent" then it cannot be 100 or higher
        if(reduction <= 0){
            throw new IllegalArgumentException("Invalid reduction values");
        }
        this.name = name;
        this.reduction = reduction;
        this.expiryDate = expiryDate;
    }

    /*
    A coupon is expired when its expiry date is before now.
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
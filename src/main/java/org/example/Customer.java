package org.example;

import java.util.ArrayList;
import java.util.List;

// Bra utöka personnr, address, email, telefonnr
public class Customer {

    private String name;
    private String personId;
    private boolean member;
    private boolean senior;
    private final List<Coupon> coupons = new ArrayList<>();

    public Customer(String name, String personId, boolean member, boolean senior){
        this.name = name;
        this.personId = personId;
        this.member = member;
        this.senior = senior;
    }

    public String getName(){
        return this.name;
    }

    public String getPersonId(){
        return this.personId;
    }

    public boolean getMembershipStatus(){
        return this.member;
    }

    public boolean getSeniorStatus(){
        return this.senior;
    }

    public void addCoupon(Coupon coupon){
        if (coupon == null){
            throw new IllegalArgumentException("Coupon must not be null.");
        }
        coupons.add(coupon);
    }

    // First coupon that has not been redeemed, or null if none.
    public Coupon getFirstAvailableCoupon(){
        for (Coupon coupon : coupons){
            if (!coupon.isRedeemed()){
                return coupon;
            }
        }
        return null;
    }

}

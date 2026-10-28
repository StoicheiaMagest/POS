package org.example;

import java.util.LinkedList;

// Bra utöka personnr, address, email, telefonnr
public class Customer {

    private String name;
    private String personId;
    private boolean member;
    private boolean senior;
    private LinkedList<Coupon> coupons = new LinkedList<>();

    public Customer(String name, String personId, boolean member, boolean senior){
        this.name = name;
        this.personId = personId;
        this.member = member;
        this.senior = senior;
        
    }

    public void addCoupon(Coupon coupon){
        coupons.add(coupon);
    }

    public LinkedList<Coupon> getCoupons(){
        return coupons;
    }
    
    public long useCouponMethod(Coupon coupon){
        if(!coupons.contains(coupon)){
            throw new NullPointerException();
        }
        coupons.remove(coupon);
        return coupon.getReduction();
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
}

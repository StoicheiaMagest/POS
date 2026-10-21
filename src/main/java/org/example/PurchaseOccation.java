package org.example;

public class PurchaseOccation {

    private Account storeBankAccount;
    private Basket customerBasket;
    private double totalCost;   //ändra till int när bakset ändrar från double till int?
    private Discount discount;
    private Membership membership;

    public PurchaseOccation(Account account, Basket basket, Discount discount){
        this.storeBankAccount = account;
        this.customerBasket = basket;
        totalCost = basket.getTotalPrice();
        this.discount = discount;
    }

//    public boolean pay(Card customerCard){
//
//        if ( customerCard.pay(totalCost) ){
//            return true;
//        }
//
//        return false;
//
//
//    }


    

    public double getTotalCost() {  //ändra till int när bakset ändrar från double till int?
        return totalCost;
    }





}



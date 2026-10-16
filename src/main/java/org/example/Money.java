package org.example;

public class Money {

    int amount;


    public Money( int amount){
        if (amount >= 0) {
            this.amount = amount;
        }
    }


    public int getAmount(){
        return this.amount;
    }

}

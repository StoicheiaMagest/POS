package org.example;

public class Scanner {

    Basket basket;

    public Scanner(){
        this.basket = new Basket();

    }

    public void add(Item item){
        basket.addItem( item );

    }

    public double getTotalPrice(){
        return basket.getTotal();
    }

}

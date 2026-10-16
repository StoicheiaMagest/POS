package org.example;

import java.util.ArrayList;

public class Scanner {

//    Basket basket;

    public Scanner(){
 //       this.basket = new Basket();

    }

    // Kan vara tillämpbart om man kör självscanning
    //public void add(Item item){
   //     basket.addItem( item );

   // }

    public int scan(Item item){
        return item.getPrice();
    }



}

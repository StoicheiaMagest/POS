package org.example;

import java.util.ArrayList;

public class Scanner {

    private final Basket basket;

    public Scanner(Basket basket) {
        this.basket = basket;
    }

    public int scan(Item item) {
        basket.addItem(item);
        return item.getPrice();
    }
}

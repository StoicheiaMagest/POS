package org.example;

import java.util.ArrayList;

public class Basket {
    private double totalPrice;
    private ArrayList<Item> items;

    public Basket() {
        this.items = new ArrayList<Item>();
        this.totalPrice = 0;
    }

    public void addItem(Item item) {
        this.items.add(item);
        this.totalPrice = this.totalPrice+item.getPrice();
    }

    public void removeItem(Item item) {
        this.items.remove(item);
        this.totalPrice = this.totalPrice-item.getPrice();
    }
    public double getTotalPrice() {
        return totalPrice;
    }

}

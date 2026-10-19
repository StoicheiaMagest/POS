
package org.example;

import java.util.ArrayList;

public class Basket {
    private int totalPrice;
    private ArrayList<Item> items;

    public Basket() {
        this.items = new ArrayList<>();
        this.totalPrice = 0;
    }

    public void addItem(Item item) {
        this.items.add(item);
        this.totalPrice = this.totalPrice + item.getPrice();
    }

    public void removeItem(Item item) {
        if (this.items.remove(item)) {
            this.totalPrice = this.totalPrice - item.getPrice();
        }
    }

    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}

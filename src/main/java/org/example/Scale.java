package org.example;

public class Scale {

    public Scale(){

    }

    public int weightPrice(WeightedItem item) {
        int weight = item.getWeight();
        int kgPrice = item.getPrice();
        return weight * kgPrice / 1000;
    }
}
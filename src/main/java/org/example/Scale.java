package org.example;

public class Scale {

    public Scale(){

    }

    public int weightPrice(WeighedItem item) {
        return item.getPrice();
    }

    // Weighs a copy of the registry template with the measured grams.
    public WeighedItem weigh(WeighedItem template, int grams) {
        return template.withWeight(grams);
    }
}
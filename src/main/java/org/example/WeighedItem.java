package org.example;

public class WeighedItem extends Item {
    final double weight;

    public WeighedItem(String name, double price, VatRate vatRate, double weight, int articleId) {
        super(name, price, vatRate, articleId);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
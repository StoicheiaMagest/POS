package org.example;

public abstract class Item {
    private final int articleId;
    private final String name;
    private final double price;
    private final VatRate vatRate;

    public Item(String name, double price, VatRate vatRate, int articleId) {
        this.name = name;
        this.price = price;
        this.vatRate = vatRate;
        this.articleId = articleId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public VatRate getVatRate() {
        return vatRate;
    }

    public int getArticleId() {
        return articleId;
    }
}
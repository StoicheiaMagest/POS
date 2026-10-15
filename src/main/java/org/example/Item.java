package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public abstract class Item {
    protected final int articleId;
    protected final String name;
    protected final int price;
    protected final VatRate vatRate;

    public Item(String name, int price, VatRate vatRate, int articleId) {
        this.name = name;
        this.price = price;
        this.vatRate = vatRate;
        this.articleId = articleId;
    }

    public abstract int getPrice(int price);

    public abstract String getName();

    public abstract int getPrice();

    public abstract VatRate getVatRate();

    public abstract int getArticleId();
}
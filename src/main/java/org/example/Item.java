package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public abstract class Item {
    protected final int articleNumber;
    protected final String name;
    protected final int price;
    protected final Vat vat;

    public Item(String name, int price, Vat vat, int articleId) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.articleNumber = articleId;
    }

    public abstract String getName();

    public abstract int getPrice();

    public abstract Vat getVatRate();

    public abstract int getArticleNumber();
}
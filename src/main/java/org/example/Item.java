package org.example;

import java.util.Objects;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public abstract class Item {
    protected final int articleNumber;
    protected final String name;
    protected final int price;
    protected final Vat vat;
    protected final DiscountType discountType;

    public Item(String name, int price, Vat vat, int articleId) {
        this(name, price, vat, articleId, DiscountType.NONE);
    }

    public Item(String name, int price, Vat vat, int articleId, DiscountType discountType) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.articleNumber = articleId;
        this.discountType = Objects.requireNonNull(discountType, "Discount type must not be null.");
    }

    public String getName() {
        return name;
    };

    public abstract int getPrice();

    public abstract Vat getVatRate();

    public int getArticleNumber() {
        return articleNumber;
    };

    public abstract long getInclusivePrice();

    public DiscountType getDiscountType() {
        return discountType;
    }
}
package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class UnitPricedArticle extends Item {

    private final int amount;

    public UnitPricedArticle(String name, int price, VatRate vatRate, int articleId, int amount) {
        super(name, price, vatRate, articleId);
        this.amount = 1;
    }

    @Override
    public int getPrice(int price) {
        return price * amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public VatRate getVatRate() {
        return vatRate;
    }

    @Override
    public int getArticleId() {
        return articleId;
    }
}
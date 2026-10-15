package org.example;

public class UnitPricedArticle extends Item {

    public UnitPricedArticle(String name, double price, VatRate vatRate, int articleId) {
        super(name, price, vatRate, articleId);
    }
}
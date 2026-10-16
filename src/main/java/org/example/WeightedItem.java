package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class WeightedItem extends Item {
    final int weight;
    final int pricePerKg;

    public WeightedItem(String name, int price, VatRate vatRate, int weight, int articleId, int pricePerKg) {
        super(name, price, vatRate, articleId);
        this.weight = weight;
        this.pricePerKg = pricePerKg;
    }

    @Override
    public int getPrice(int price){
        return price * pricePerKg;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public int getPrice(){
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


    public int getWeight() {
        return weight;
    }

    public int getPricePerKg() {
        return pricePerKg;
    }



}

package org.example;

// All units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class WeightedItem extends Item {

    private final int weightInGrams;
    private int pricePerKg;

    public WeightedItem(String name, int pricePerKg, Vat vat, int weightInGrams, int articleId) {
        super(name, pricePerKg, vat, articleId);
        this.weightInGrams = weightInGrams;
        this.pricePerKg = pricePerKg;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        // Calculates the total base price (exclusive of VAT) in cents
        // based on the price per kg and the weight in grams.
        // Cast to long prevents integer overflow during multiplication.
        //return (int) ((long) price * weightInGrams / 1000);
        return pricePerKg;
    }

    @Override
    public Vat getVatRate() {
        return vat;
    }

    @Override
    public int getArticleId() {
        return articleId;
    }

    public int getWeight() {
        return weightInGrams;
    }

    /**
     * Calculates the VAT amount for this weighed item in cents using integer math.
     */
    public long getVatAmount() {
        return vat.calculateVatAmount(getPrice());
    }

    /**
     * Calculates the total price including VAT for this weighed item in cents.
     */
    public long getInclusivePrice() {
        return vat.calculateInclusivePrice(getPrice());
    }
}
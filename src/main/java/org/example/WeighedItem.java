package org.example;

// All units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class WeighedItem extends Item {

    private final long weightInGrams;
    private final long pricePerKg;

    public WeighedItem(String name, long pricePerKg, Vat vat, long weightInGrams, int articleId) {
        super(name, pricePerKg, vat, articleId);
        this.weightInGrams = weightInGrams;
        this.pricePerKg = pricePerKg;
    }

    @Override
    public Vat getVatRate() {
        return vat;
    }

    public long getWeight() {
        return weightInGrams;
    }

    /**
     * Calculates the VAT amount for this weighed item in cents using integer math.
     */
    public long getVatAmount() {
        return vat.calculateVatAmount(getPrice());
    }

    @Override
    public long getPrice() {
        // Calculate the base price in cents using weight in grams.
        //return (int) ((long) pricePerKg * weightInGrams / 1000);
        return pricePerKg * weightInGrams / 1000;
    }

    public long getPricePerKg() {
        return pricePerKg;
    }

    /**
     * Calculates the total price including VAT for this weighed item in cents.
     */
    @Override
    public long getInclusivePrice() {
        return vat.calculatePriceWithVat(getPrice());
    }
}
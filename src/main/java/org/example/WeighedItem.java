package org.example;

// All units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class WeighedItem extends Item {

    private final int weightInGrams;
    private final int pricePerKg;

    public WeighedItem(String name, int pricePerKg, Vat vat, int weightInGrams, int articleId) {
        this(name, pricePerKg, vat, weightInGrams, articleId, DiscountType.NONE);
    }

    public WeighedItem(String name, int pricePerKg, Vat vat, int weightInGrams, int articleId,
                       DiscountType discountType) {
        super(name, pricePerKg, vat, articleId, allowedForWeighed(discountType));
        this.weightInGrams = weightInGrams;
        this.pricePerKg = pricePerKg;
    }

    // 3 for 2 counts items, which makes no sense for something sold by weight.
    private static DiscountType allowedForWeighed(DiscountType discountType) {
        if (discountType == DiscountType.THREE_FOR_TWO) {
            throw new IllegalArgumentException("3 for 2 cannot be used on a weighed item.");
        }
        return discountType;
    }

    @Override
    public Vat getVatRate() {
        return vat;
    }

    public int getWeight() {
        return weightInGrams;
    }

    // Copy of this item with a measured weight, so the registry can hold one template.
    public WeighedItem withWeight(int grams) {
        if (grams <= 0) {
            throw new IllegalArgumentException("Weight must be positive.");
        }
        return new WeighedItem(name, pricePerKg, vat, grams, articleNumber, discountType);
    }

    /**
     * Calculates the VAT amount for this weighed item in cents using integer math.
     */
    public long getVatAmount() {
        return vat.calculateVatAmount(getPrice());
    }

    @Override
    public int getPrice() {
        // Calculate the base price in cents using weight in grams.
        return (int) ((long) pricePerKg * weightInGrams / 1000);
    }

    public int getPricePerKg() {
        return pricePerKg;
    }

    /**
     * Calculates the total price including VAT for this weighed item in cents.
     */
    @Override
    public long getInclusivePrice() {
        return vat.calculateInclusivePrice(getPrice());
    }
}
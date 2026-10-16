package org.example;

// All units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

<<<<<<< HEAD:src/main/java/org/example/WeightedItem.java
public class WeightedItem extends Item {
    final int weight;
    final int pricePerKg;

    public WeightedItem(String name, int price, VatRate vatRate, int weight, int articleId, int pricePerKg) {
        super(name, price, vatRate, articleId);
        this.weight = weight;
        this.pricePerKg = pricePerKg;
=======
public class WeighedItem extends Item {

    private final int weightInGrams;

    public WeighedItem(String name, int pricePerKg, Vat vat, int weightInGrams, int articleId) {
        super(name, pricePerKg, vat, articleId);
        this.weightInGrams = weightInGrams;
>>>>>>> e38d41672e20865044a61815a488cf4864ffa4a8:src/main/java/org/example/WeighedItem.java
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
        return (int) ((long) price * weightInGrams / 1000);
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
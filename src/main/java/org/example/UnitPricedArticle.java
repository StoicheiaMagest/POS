package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class UnitPricedArticle extends Item {

    public UnitPricedArticle(String name, int priceExclCents,
                             Vat vat, int articleId) {
        super(name, priceExclCents, vat, articleId);
    }

    @Override
    public int getPrice() {
        return price; // Returns the base price (exclusive of VAT) in cents
    }

    @Override
    public Vat getVatRate() {
        return vat;
    }

    /**
     * Calculates the VAT amount for this item in cents using integer math.
     */
    public long getVatAmount() {
        return vat.calculateVatAmount(price);
    }

    /**
     * Calculates the total price including VAT for this item in cents.
     */
    
    @Override
    public long getInclusivePrice() {
        return vat.calculateInclusivePrice(price);
    }
}
package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public class Item {
    protected final int articleNumber;
    protected final String name;
    protected final long price;
    protected final Vat vat;
    protected String discountType;

    public Item(String name, long price, Vat vat, int articleId) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.articleNumber = articleId;
    }

    public Item(String name, long price, Vat vat, int articleId, String discountType) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.articleNumber = articleId;
        this.discountType = discountType;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price; // Returns the base price (exclusive of VAT) in cents
    }

    public Vat getVatRate() {
        return vat;
    }

    public String getDiscountType(){
        return discountType;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    public long getInclusivePrice() {
        return vat.calculatePriceWithVat(getPrice());
    }}
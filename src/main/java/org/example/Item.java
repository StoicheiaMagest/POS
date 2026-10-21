package org.example;

// ALl units must be in integers, especially weight and money because
// according to Become a Java Craftsman in 70 Examples says so

public abstract class Item {
    protected final int articleNumber;
    protected final String name;
    protected final String fullDescription;
    protected final int price;
    protected final Vat vat;

    public Item(String name, int price, Vat vat, int articleId,
                String fullDescription) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.articleNumber = articleId;
        this.fullDescription = fullDescription;
    }

    public String getName() {
        return name;
    };

    public String getFullDescription() {
        return fullDescription;
    }

    public abstract int getPrice();

    /**
     * Total price including VAT for this article's current line, in cents.
     */
    public long getInclusivePrice() {
        return vat.calculateInclusivePrice(getPrice());
    }

    public abstract Vat getVatRate();

    public int getArticleNumber() {
        return articleNumber;
    };
}
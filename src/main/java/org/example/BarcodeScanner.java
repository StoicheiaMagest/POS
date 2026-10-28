
package org.example;

public class BarcodeScanner {

    private final Basket basket;
    private final ArticleRegistry registry;

    public BarcodeScanner(Basket basket, ArticleRegistry registry) {
        this.basket = basket;
        this.registry = registry;
    }

    public long scan(int articleNumber) {
        Item item = registry.findByArticleNumber(articleNumber);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Article not found: " + articleNumber
            );
        }

        basket.addItem(item);
        return item.getPrice();
    }
}

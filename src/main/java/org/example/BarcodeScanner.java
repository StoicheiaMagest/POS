
package org.example;

public class BarcodeScanner {

    private final ArticleRegistry registry;

    public BarcodeScanner(ArticleRegistry registry) {
        this.registry = registry;
    }

    // Returns the scanned item so it can be added to the current basket.
    public Item scan(int articleNumber) {
        Item item = registry.findByArticleNumber(articleNumber);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Article not found: " + articleNumber
            );
        }

        return item;
    }
}


package org.example;

public class BarcodeScanner {

    private final ArticleRegistry registry;

    public BarcodeScanner(ArticleRegistry registry) {
        this.registry = registry;
    }

    //Scans a barcode and returns the matching item from the registry.
    //The scanner does not know any basket, adding to a basket is done by the caller.
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

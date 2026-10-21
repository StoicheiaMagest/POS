package org.example;

/**
 * A barcode scanner at the cash register. It resolves a scanned article
 * number to the registry article and nothing else — it is owned by the
 * {@link PurchaseOccasion} and never touches the customer's basket
 * (spec &sect;4.10).
 */
public class BarcodeScanner {

    private final ArticleRegistry registry;

    public BarcodeScanner(ArticleRegistry registry) {
        this.registry = registry;
    }

    /**
     * Scans a barcode and returns the matching article.
     *
     * @param articleNumber the scanned barcode
     * @return the registered article
     * @throws IllegalArgumentException if the barcode is unknown
     */
    public Item scan(int articleNumber) {
        Item item = registry.findByArticleNumber(articleNumber);
        if (item == null) {
            throw new IllegalArgumentException(
                    "Article not found: " + articleNumber);
        }
        return item;
    }
}

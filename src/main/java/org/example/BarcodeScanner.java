package org.example;

/**
 * A barcode scanner at the cash register. A barcode is an integer whose
 * digits carry information at fixed positions (like a scale printout
 * label): values below {@value #WEIGHTED_BARCODE_FLOOR} are plain article
 * numbers, larger ones encode the weight in grams in their last five
 * digits, e.g. 900300 is article 9 weighed at 300 g. The scanner resolves
 * a barcode to the sale item and nothing else — it is owned by the
 * {@link PurchaseOccasion}
 */
public class BarcodeScanner {

    /** Divisor separating article id digits from the grams digits. */
    private static final int GRAMS_SCALE = 100000;

    /** Barcodes at or above this carry a weight in their last digits. */
    private static final int WEIGHTED_BARCODE_FLOOR = GRAMS_SCALE;

    private final ArticleRegistry registry;

    public BarcodeScanner(ArticleRegistry registry) {
        this.registry = registry;
    }

    /**
     * Scans a barcode and returns the matching sale item; weighed barcodes
     * yield an article carrying the encoded weight.
     *
     * @param barcode the scanned barcode
     * @return the registered article (a concrete weighed instance when the
     *         barcode encodes a weight)
     * @throws IllegalArgumentException if the article part is unknown
     */
    public Item scan(int barcode) {
        Item template = lookupArticle(articleIdOf(barcode));
        if (isWeighedBarcode(barcode)) {
            return weighedCopyOf(template, gramsOf(barcode));
        }
        return template;
    }

    /**
     * Builds the barcode of a basket entry: the article number for unit
     * articles, the article number plus encoded weight for weighed goods.
     *
     * @param item the saleable item to encode
     * @return its barcode
     */
    public static int barcodeOf(Item item) {
        if (item instanceof WeighedItem weighed) {
            return item.getArticleNumber() * GRAMS_SCALE + weighed.getWeight();
        }
        return item.getArticleNumber();
    }

    private Item lookupArticle(int articleId) {
        Item template = registry.findByArticleNumber(articleId);
        if (template == null) {
            throw new IllegalArgumentException(
                    "Article not found: " + articleId);
        }
        return template;
    }

    private static boolean isWeighedBarcode(int barcode) {
        return barcode >= WEIGHTED_BARCODE_FLOOR;
    }

    /**
     * The article id: the whole barcode for unit articles, the digits above
     * the fixed five-digit grams field for weighed barcodes.
     */
    private static int articleIdOf(int barcode) {
        return isWeighedBarcode(barcode) ? barcode / GRAMS_SCALE : barcode;
    }

    /** The weight: the last five digits of a weighed barcode. */
    private static int gramsOf(int barcode) {
        return barcode % GRAMS_SCALE;
    }

    private static Item weighedCopyOf(Item template, int grams) {
        return new WeighedItem(template.getName(), template.getPrice(),
                template.getVatRate(), grams, template.getArticleNumber(),
                template.getFullDescription());
    }
}

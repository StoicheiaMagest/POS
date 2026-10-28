package org.example;

/*
 * The store side of a purchase, like the cashier standing next to the customer.
 * This class is only a holder: it keeps the store's registry, bank account,
 * discount handler and barcode scanner together so they can be passed into
 * a purchase as one input. It creates no discounts itself.
 */
public class Store {

    private final ArticleRegistry articleRegistry;
    private final Account storeBankAccount;
    private final DiscountHandler discountHandler;
    private final BarcodeScanner barcodeScanner;

    public Store(ArticleRegistry articleRegistry,
                 Account storeBankAccount,
                 DiscountHandler discountHandler,
                 BarcodeScanner barcodeScanner) {
        this.articleRegistry = articleRegistry;
        this.storeBankAccount = storeBankAccount;
        this.discountHandler = discountHandler;
        this.barcodeScanner = barcodeScanner;
    }

    public ArticleRegistry getArticleRegistry() {
        return articleRegistry;
    }

    public Account getStoreBankAccount() {
        return storeBankAccount;
    }

    public DiscountHandler getDiscountHandler() {
        return discountHandler;
    }

    public BarcodeScanner getBarcodeScanner() {
        return barcodeScanner;
    }
}

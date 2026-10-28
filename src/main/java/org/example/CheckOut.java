package org.example;

/*
 * One occasion when the cash register is in use for one customer.
 * The CheckOut owns the basket being filled and scans goods into it.
 * Pricing and payment are NOT done here, that is PurchaseOccasion's job.
 */
public class CheckOut {

    private final Store store;
    private final Basket basket = new Basket();

    public CheckOut(Store store) {
        this.store = store;
    }

    /*
     * For every scanned article number, the barcode scanner looks up the item
     * in the store's registry and the item is added to this checkout's basket.
     */
    public void scanGoods(int... articleNumbers) {
        for (int articleNumber : articleNumbers) {
            Item item = store.getBarcodeScanner().scan(articleNumber);
            basket.addItem(item);
        }
    }

    public Basket getBasket() {
        return basket;
    }

    /*
     * Ends the checkout: the scanned basket, the customer and the store are
     * handed over to a new PurchaseOccasion, which does the pricing and payment.
     */
    public PurchaseOccasion pay(Customer customer) {
        return new PurchaseOccasion(basket, customer, store);
    }
}

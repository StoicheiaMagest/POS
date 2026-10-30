package org.example;

import java.time.LocalDateTime;

/*
 * Prints example receipts for two real purchases, to see what a Receipt looks like.
 * It goes through the real flow: basket, discounts, payment, and only then the receipt.
 * The receipt is sent to a mock printer (a hand-written javax.print.PrintService)
 * and afterwards whatever came out of that printer is shown in the terminal.
 * Run it with: java -cp target/classes plus target/test-classes org.example.ReceiptDemo
 */
public class ReceiptDemo {

    // UnitPricedArticle has no constructor for a discount type, so this sets it.
    private static class DiscountedArticle extends UnitPricedArticle {
        DiscountedArticle(String name, long priceExclCents, Vat vat, int articleId, String discountType) {
            super(name, priceExclCents, vat, articleId);
            this.discountType = discountType;
        }
    }

    public static void main(String[] args) throws Exception {
        Store store = createStore();
        FakePrinter printer = new FakePrinter();
        ReceiptPrinter receiptPrinter = new ReceiptPrinter(printer);

        printReceipt(receiptPrinter, 1, fullReceiptPurchase(store));
        printReceipt(receiptPrinter, 2, plainPurchase(store));
        printReceipt(receiptPrinter, 3, expensivePurchase(store));

        // The mock printer caught everything instead of putting it on paper.
        System.out.println(printer.printedJobs().size() + " receipts came out of the printer:");
        for (String printedReceipt : printer.printedJobs()) {
            System.out.println();
            System.out.println(printedReceipt);
        }
    }

    private static Store createStore() {
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        return new Store(registry, new Account(), new DiscountHandler(), scanner);
    }

    // A senior member with a coupon, buying three cokes (3 for 2), coffee and bread.
    private static PurchaseOccasion fullReceiptPurchase(Store store) {
        Item coke = new DiscountedArticle("Coke", 1250L, Vat.FOOD, 111, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(coke);
        basket.addItem(coke);
        basket.addItem(coke);
        basket.addItem(new UnitPricedArticle("Coffee", 4900L, Vat.FOOD, 114));
        basket.addItem(new UnitPricedArticle("Bread", 2500L, Vat.FOOD, 222));

        Customer customer = new Customer("Greta", "19400101-1234", true, true);
        customer.addCoupon(new Coupon("20krOff", 2000L, LocalDateTime.now().plusDays(1)));
        customer.setCard(new Card(5555L, new Account(100000L)));

        return new PurchaseOccasion(basket, customer, store);
    }

    // A customer without membership, buying one article with a very long name.
    private static PurchaseOccasion plainPurchase(Store store) {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Organic oat drink with extra calcium 1L", 2900L, Vat.FOOD, 113));
        basket.addItem(new UnitPricedArticle("Soap", 2500L, Vat.STANDARD, 444));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        customer.setCard(new Card(6666L, new Account(100000L)));

        return new PurchaseOccasion(basket, customer, store);
    }

    // The same long name, but the price is 1 000 000 kr including VAT,
    // to see how a long amount leaves less room for the name.
    private static PurchaseOccasion expensivePurchase(Store store) {
        // 89 285 714 ore + 12 % food VAT = exactly 100 000 000 ore
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Organic oat drink with extra calcium 1L", 89285714L, Vat.FOOD, 113));

        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        customer.setCard(new Card(7777L, new Account(200000000L)));

        return new PurchaseOccasion(basket, customer, store);
    }

    // The receipt is only made when the payment went through.
    private static void printReceipt(ReceiptPrinter receiptPrinter, int receiptNumber, PurchaseOccasion purchase)
            throws Exception {
        purchase.applyDiscounts();
        if (!purchase.payPurchase()) {
            System.out.println("Payment failed, no receipt.");
            return;
        }
        Receipt receipt = new Receipt(receiptNumber, LocalDateTime.now(), "My Store", purchase);
        receiptPrinter.print(receipt);
    }
}

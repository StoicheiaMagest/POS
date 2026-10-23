package org.example;

public class PurchaseOccasion {

    private Account storeBankAccount;
    private Basket customerBasket;
    private Customer customer;
    private Discount[] discounts;
    private long totalPrice; // cents



    public PurchaseOccasion(
            Basket basket,
            Customer customer,
            Account storeBankAccount,
            Discount... discounts
    ) {
        this.customerBasket = basket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discounts = discounts;
        this.totalPrice = basket.getTotalPrice();
    }

    public Basket getBasket() {
        return customerBasket;
    }

    // Returns the discounted price in cents.
    public long applyDiscount(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        long priceAfterDiscount =
                (long) totalPrice * (100 - discountPercent);

        // Add 50 before division to round to the nearest cent.
        return (int) ((priceAfterDiscount + 50) / 100);
    }


    public String printReceipt() {
        return "Kvitto\n"
                + "Antal varor: " + customerBasket.getItems().size() + "\n"
                + "Totalt: " + Money.format(customerBasket.getTotalPrice(), ',') + " SEK";
    }


}


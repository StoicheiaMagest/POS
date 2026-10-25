package org.example;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PurchaseOccasion {

    private Basket customerBasket;
    private Customer customer;
    private Account storeBankAccount;
    private Discount discount = new Discount();
    private boolean paid;
    //private long totalPrice; // cents

    public PurchaseOccasion(Basket customerBasket, Customer customer,
            Account storeBankAccount, Discount discount
    ) {
        this.customerBasket = customerBasket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
        this.discount = discount;
    }

    public Basket getCustomerBasket() {
        return customerBasket;
    }

    public Customer getCustomer() {
        return customer;
    }

    public long getSubtotal(){
        return customerBasket.getTotalPrice();
    }

    public Map<String, Long> getDiscounts(){
        return discount.getAllDiscounts(customerBasket, customer);
    }

    public long getTotalPrice() {
        long off = getDiscounts().values().stream().mapToLong(Long::longValue).sum();
        return Math.max(0, getSubtotal() - off);
    }

    public boolean pay(Card card) {
        if (paid || !card.pay(getTotalPriceForPurchace(), storeBankAccount)) {
            return false;
        }
        paid = true;
        return true;
    }

    public long getTotalPriceForPurchace() {
        return getTotalPriceForPurchace();
    }

}


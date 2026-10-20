package org.example;

import java.util.ArrayList;

public class Order {

    private Scanner scanner;
    private Account storeBankAccount;
    private Basket customerBasket;
    private int totalCost;

    public Order(Account account, Scanner scanner, Basket basket){
        this.scanner = scanner;
        this.storeBankAccount = account;
        this.customerBasket = basket;
    }

//    public boolean pay(Card customerCard){
//
//        if ( customerCard.pay(totalCost) ){
//            return true;
//        }
//
//        return false;
//
//
//    }

    public void scanItems(Basket basket){
        ArrayList<Item> items = basket.getItems();
        if(items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Error, there's no items to scan");
        }

        int total = 0;
        for (int i=0; i < items.size() - 1; i++){
            scanner.scan(items.get(i).getArticleId());
            int itemPrice = items.get( i ).getPrice() ;
            total += itemPrice;

        }
        this.totalCost = total;
    }

    public int getTotalCost() {
        return totalCost;
    }





}



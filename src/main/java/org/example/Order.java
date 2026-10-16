package org.example;

import java.util.ArrayList;

public class Order {

    private Scanner scanner;
    private Account storeBankAccount;
    private Basket customerBasket;
    private int total;

    public Order(Account account, Scanner scanner, Basket basket){
        this.scanner = scanner;
        this.storeBankAccount = account;
        this.customerBasket = basket;

    }

    public void pay(Card customerCard){



    }

    public void scanItems(Basket basket){
        ArrayList<Item> items = basket.getItems();

        int total = 0;
        for (int i=0; i < items.size() - 1; i++){

            int itemPrice = items.get( i ).getPrice() ;
            total += itemPrice;

        }
        this.total = total;
    }







}



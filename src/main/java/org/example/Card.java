package org.example;

public class Card {

    Account account;

    public Card (Account account){
        this.account = account;

    }


    public boolean pay(long amount, Account recipient) {
        return account.transfer(amount, recipient);
    }


}

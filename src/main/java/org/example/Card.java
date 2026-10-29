package org.example;

public class Card {

    private final long cardId;
    Account account;

    public Card (long cardId, Account account){
        this.cardId = cardId;
        this.account = account;

    }

    public long getCardId(){
        return cardId;
    }


    public boolean pay(long amount, Account recipient) {
        return account.transfer(amount, recipient);
    }


}

package org.example;

public class Account {

    private int balance;

    public Account(){
        this.balance = 0;
    }

    public boolean transfer(int amount, Account account){



    }


    public void addToBalance(Money money){
        balance += money.getAmount();

    }

    public int removeFromBalance(int amount){
        if (amount > balance){
            throw new RuntimeException("Cannot remove greater amount than balance");
        }
        this.balance -= amount;
        return amount;
    }

}

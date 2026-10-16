package org.example;

public class Account {

    private int balance;

    public Account(){
        this.balance = 0;
    }

    public boolean transfer(int amount, Account account){
        boolean canTransfer = checkCanTransfer(amount);

        if (canTransfer){
            this.balance -= amount;
            account.addToBalance( amount );
            return true;
        }

        return false;

    }

    public void addToBalance(int amount){
        balance += amount;
    }


    public void addToBalance(Money money){
        balance += money.getAmount();

    }

    public boolean checkCanTransfer(int amount){
        if (amount > balance){
            return false;
        }
        return true;
    }

}

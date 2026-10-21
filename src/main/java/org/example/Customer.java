package org.example;

import javax.xml.namespace.QName;

// Bra utöka personnr, address, email, telefonnr
public class Customer {

    private String name;
    private String personId;
    private boolean member;
    private boolean senior;

    public Customer(String name, String personId, boolean member, boolean senior){
        this.name = name;
        this.personId = personId;
        this.member = member;
        this.senior = senior;
    }

    public String getName(){
        return this.name;
    }

    public String getPersonId(){
        return this.personId;
    }

    public boolean getMembershipStatus(){
        return this.member;
    }

    public boolean getSeniorStatus(){
        return this.senior;
    }

}

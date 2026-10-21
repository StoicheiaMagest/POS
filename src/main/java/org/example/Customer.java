package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The shop's customer: identity (name, person number), membership data
 * (joined at the register per spec &sect;4.11), payment cards and where the
 * customer currently is in the flow (spec &sect;6).
 */
public class Customer {

    private final String name;
    private final String personId;
    private LocalDate birthday;
    private boolean member;
    private String memberNumber;
    private CustomerStatus status = CustomerStatus.IN_STORE;
    private final List<PaymentCard> cards = new ArrayList<>();

    public Customer(String name, String personId) {
        this.name = name;
        this.personId = personId;
    }

    public String getName() {
        return this.name;
    }

    public String getPersonId() {
        return this.personId;
    }

    /**
     * Birthday recorded when joining the membership, null while a guest.
     *
     * @return birthday or null
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    public boolean isMember() {
        return this.member;
    }

    /**
     * Membership number in format {@code M} + 6 digits, null while a guest.
     *
     * @return member number or null
     */
    public String getMemberNumber() {
        return memberNumber;
    }

    /**
     * Registers the customer as a member with the given birthday and
     * generated member number (spec &sect;4.11).
     *
     * @param birthday     the birthday entered at the register
     * @param memberNumber generated membership number
     */
    public void joinMembership(LocalDate birthday, String memberNumber) {
        this.birthday = birthday;
        this.memberNumber = memberNumber;
        this.member = true;
    }

    /**
     * Current location of the customer in the shopping flow.
     *
     * @return IN_STORE or AT_REGISTER
     */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Moves the customer to the cash register (spec &sect;4.9).
     */
    public void goToRegister() {
        this.status = CustomerStatus.AT_REGISTER;
    }

    /**
     * Issues a payment card to this customer (spec &sect;4.14).
     *
     * @param card the newly created card
     */
    public void addCard(PaymentCard card) {
        cards.add(card);
    }

    /**
     * All payment cards held by this customer.
     *
     * @return a defensive copy of the card list
     */
    public List<PaymentCard> getCards() {
        return Collections.unmodifiableList(new ArrayList<>(cards));
    }
}

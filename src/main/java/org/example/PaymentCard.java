package org.example;

/**
 * A bank card: identity (id + issuer) plus a linked bank account. Paying is
 * an account-to-account transfer from the linked account to the recipient
 * (spec &sect;6, consolidates the former Card/PaymentCard duplicates).
 */
public class PaymentCard {

    private final int cardId;
    private final String issuer;
    private final Account account;

    public PaymentCard(int cardId, String issuer, Account account) {
        this.cardId = cardId;
        this.issuer = issuer;
        this.account = account;
    }

    public int getCardId() {
        return cardId;
    }

    public String getIssuer() {
        return issuer;
    }

    /**
     * Current balance of the linked account, in cents.
     *
     * @return balance in cents
     */
    public int getBalance() {
        return account.getBalance();
    }

    /**
     * Pays the given amount by transferring from the linked account.
     *
     * @param amount    amount in cents
     * @param recipient receiving account (the store)
     * @return true when the transfer succeeded; false when the balance was
     *         insufficient (nothing is transferred then)
     */
    public boolean pay(int amount, Account recipient) {
        return account.transfer(amount, recipient);
    }
}

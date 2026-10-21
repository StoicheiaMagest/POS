package org.example;

/**
 * Outcome of a card payment attempt on a purchase occasion
 * (spec &sect;4.13).
 */
public enum PaymentResult {
    /** The transfer executed and reduced the amount due. */
    SUCCESS,
    /** The card account cannot cover the requested amount. */
    INSUFFICIENT_BALANCE,
    /** The requested amount exceeds what is still due. */
    EXCEEDS_AMOUNT_DUE
}

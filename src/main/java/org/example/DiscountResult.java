package org.example;

/**
 * Outcome of a membership-discount attempt on a purchase occasion
 * (spec &sect;4.12).
 */
public enum DiscountResult {
    /** The 20 % discount was applied to the amount due. */
    APPLIED,
    /** Only members may claim the discount. */
    NOT_A_MEMBER,
    /** One discount per purchase occasion. */
    ALREADY_APPLIED,
    /** Discount cannot be claimed after payment has started (§4.3). */
    PAYMENT_STARTED
}

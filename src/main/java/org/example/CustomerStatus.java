package org.example;

/**
 * Where the customer currently is in the shopping flow (spec &sect;3).
 */
public enum CustomerStatus {
    /** Walking the aisles with a basket. */
    IN_STORE,
    /** Queued at the cash register with a purchase occasion. */
    AT_REGISTER
}

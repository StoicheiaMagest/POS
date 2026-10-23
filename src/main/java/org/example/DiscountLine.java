package org.example;

// A single discount: its description, the VAT-inclusive amount in cents,
// and the VAT rate of the article it applies to.
public record DiscountLine(String name, long amount, Vat vat) {
}

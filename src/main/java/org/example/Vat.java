package org.example;

public enum Vat {
    STANDARD(2500),  // 25.00% represented as basis points
    FOOD(1200),   // 12.00%
    BOOK(600);     // 6.00%

    private final int basisPoints;

    Vat(int basisPoints) {
        this.basisPoints = basisPoints;
    }

    public int getBasisPoints() {
        return basisPoints;
    }

    /**
     * Calculates the VAT amount given a base price (exclusive of VAT) in cents.
     * Uses integer arithmetic with standard half-up rounding.
     */
    public long calculateVatAmount(long priceWithoutVat) {
        if (priceWithoutVat < 0) {
            throw new IllegalArgumentException("Reject negative prices in Vat.calculateVatAmount: " + priceWithoutVat
            );
        }
        long numerator = priceWithoutVat * basisPoints;
        // Adding 5000 before dividing by 10000 ensures standard rounding (round half-up)
        return (numerator + 5000) / 10000;
    }

    /**
     * Calculates the total price including VAT, given the exclusive price in cents.
     */
    public long calculatePriceWithVat(long priceWithoutVat) {
        return priceWithoutVat + calculateVatAmount(priceWithoutVat);
    }

    /**
     * Extracts the exclusive price from an inclusive price in cents.
     */
    public long calculatePriceWithoutVat(long priceWithVat) {
        long denominator = 10000 + basisPoints;
        long numerator = priceWithVat * 10000;
        // Round half-up for division
        return (numerator + (denominator / 2)) / denominator;
    }
}
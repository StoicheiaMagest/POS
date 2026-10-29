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

    public long calculateVatAmount(long priceWithoutVat) {
        if (priceWithoutVat < 0) {
            throw new IllegalArgumentException("priceWithoutVat cannot be negative"
            );
        }
        // Adding 5000 before dividing by 10000 ensures standard rounding (round half-up)
        long numerator = Math.addExact(Math.multiplyExact(priceWithoutVat, this.basisPoints), 5000);
        return numerator / 10000;
    }

    public long calculatePriceWithVat(long priceWithoutVat) {
        return priceWithoutVat + calculateVatAmount(priceWithoutVat);
    }

    /**
     * Extracts the exclusive price from an inclusive price in cents.
     */
    public long calculatePriceWithoutVat(long priceWithVat) {
        if (priceWithVat < 0) {
            throw new IllegalArgumentException("priceWithVat cannot be negative"
            );
        }
        long denominator = 10000 + basisPoints;
        long numerator = priceWithVat * 10000;
        // Round half-up for division
        return (numerator + (denominator / 2)) / denominator;
    }
}
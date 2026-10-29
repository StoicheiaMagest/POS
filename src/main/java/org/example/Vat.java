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
        return divideRoundingHalfUp(Math.multiplyExact(priceWithoutVat, basisPoints), 10000);

    }

    public long calculatePriceWithVat(long priceWithoutVat) {
        return priceWithoutVat + calculateVatAmount(priceWithoutVat);
    }

    public long calculatePriceWithoutVat(long priceWithVat) {
        if (priceWithVat < 0) {
            throw new IllegalArgumentException("priceWithVat cannot be negative"
            );
        }
        return divideRoundingHalfUp(Math.multiplyExact(priceWithVat, 10000), 10000 + basisPoints);

    }

    static long divideRoundingHalfUp(long numerator, long denominator) {
        return Math.addExact(numerator, denominator / 2) / denominator;
    }

}
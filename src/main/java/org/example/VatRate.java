package org.example;

public enum VatRate {
    STANDARD(0.25),
    FOOD_12(0.12),
    BOOK_6(0.06);

    private final double rate;

    VatRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
}
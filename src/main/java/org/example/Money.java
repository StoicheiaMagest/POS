package org.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Money {
    private final int amount;

    public Money(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    // Formats an amount in cents, e.g. 1120 -> "11,20".
    public static String format(long cents, char decimalSeparator) {
        return formatDecimal(cents, 2, decimalSeparator);
    }

    // Formats an integer holding `decimals` implied decimal places.
    public static String formatDecimal(long value, int decimals, char decimalSeparator) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("sv-SE"));
        symbols.setDecimalSeparator(decimalSeparator);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        formatter.setMinimumFractionDigits(decimals);
        formatter.setMaximumFractionDigits(decimals);
        return formatter.format(value / Math.pow(10, decimals));
    }
}
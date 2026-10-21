package org.example;

/**
 * Renders money amounts for the customer-facing output: two decimals with a
 * decimal point and a space before the currency code, e.g. "34.96 SEK"
 * (spec convention C4). Pure integer arithmetic — no floating point.
 */
public final class PriceFormatter {

    private static final int CENTS_PER_KRONA = 100;

    private PriceFormatter() {
    }

    /**
     * Formats an amount of cents as "X.XX SEK".
     *
     * @param cents the amount in cents, may be negative (discount lines)
     * @return the display string
     */
    public static String format(long cents) {
        String sign = cents < 0 ? "-" : "";
        long abs = Math.abs(cents);
        return String.format("%s%d.%02d SEK", sign,
                abs / CENTS_PER_KRONA, abs % CENTS_PER_KRONA);
    }
}

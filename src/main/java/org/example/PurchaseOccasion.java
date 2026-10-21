package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A purchase occasion at the cash register: it owns the barcode scanner,
 * turns the customer's basket into scanned sales lines with running totals
 * (spec &sect;4.10), and later tracks discount and card payments
 * (&sect;4.12-4.13).
 */
public class PurchaseOccasion {

    /** Membership discount in percent (spec §4.12). */
    private static final int MEMBERSHIP_DISCOUNT_PERCENT = 20;

    private final BarcodeScanner scanner;
    private final Basket customerBasket;
    private final Customer customer;
    private final Account storeBankAccount;
    private boolean scanned;
    private long amountDueCents;
    private boolean discountApplied;
    private long discountCents;
    private long paidCents;
    private boolean paymentStarted;
    private List<ScanLine> scanLines = List.of();

    public PurchaseOccasion(
            BarcodeScanner scanner,
            Basket basket,
            Customer customer,
            Account storeBankAccount
    ) {
        this.scanner = scanner;
        this.customerBasket = basket;
        this.customer = customer;
        this.storeBankAccount = storeBankAccount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Basket getBasket() {
        return customerBasket;
    }

    public Account getStoreBankAccount() {
        return storeBankAccount;
    }

    /**
     * Scans every basket article once (identical unit articles are scanned
     * as one grouped line) and returns the sales lines with cumulative
     * VAT-inclusive totals.
     *
     * @return one scan line per distinct article / weighed entry
     * @throws IllegalStateException if the basket was already scanned
     */
    public List<ScanLine> scanAll() {
        if (scanned) {
            throw new IllegalStateException(
                    "All items have already been scanned.");
        }
        List<ScanLine> lines = new ArrayList<>();
        Map<String, Integer> groupIndexByName = new HashMap<>();
        long runningTotal = 0;
        for (Item item : customerBasket.getItems()) {
            scanner.scan(item.getArticleNumber());
            if (item instanceof WeighedItem weighed) {
                runningTotal += item.getInclusivePrice();
                lines.add(new ScanLine(weighed.getWeight() + "g",
                        item.getFullDescription(), item.getInclusivePrice(),
                        runningTotal));
            } else {
                Integer index = groupIndexByName.get(item.getName());
                runningTotal += item.getInclusivePrice();
                if (index == null) {
                    lines.add(new ScanLine("1", item.getFullDescription(),
                            item.getInclusivePrice(), runningTotal));
                    groupIndexByName.put(item.getName(), lines.size() - 1);
                } else {
                    ScanLine line = lines.get(index);
                    int count = Integer.parseInt(line.getQuantity()) + 1;
                    lines.set(index, new ScanLine(String.valueOf(count),
                            line.getDescription(),
                            line.getLineTotalCents()
                                    + item.getInclusivePrice(),
                            runningTotal));
                }
            }
        }
        scanned = true;
        amountDueCents = runningTotal;
        scanLines = List.copyOf(lines);
        return lines;
    }

    /**
     * The sales lines produced by scanning, empty before scanAll.
     *
     * @return scanned lines in scan order
     */
    public List<ScanLine> getScanLines() {
        return scanLines;
    }

    /**
     * The membership discount amount actually deducted, in cents.
     *
     * @return discount in cents (0 when none)
     */
    public long getDiscountCents() {
        return discountCents;
    }

    /**
     * Total already paid by card on this occasion, in cents.
     *
     * @return sum of successful transfers
     */
    public long getPaidCents() {
        return paidCents;
    }

    /**
     * The amount still to be paid for this occasion, in VAT-inclusive cents
     * after any applied discount.
     *
     * @return remaining amount due in cents
     */
    public long getAmountDueCents() {
        return amountDueCents;
    }

    /**
     * Applies the one-off membership discount of
     * {@value #MEMBERSHIP_DISCOUNT_PERCENT} % to the amount due, rounded
     * half-up (spec &sect;4.12).
     *
     * @return the outcome of the attempt
     */
    public DiscountResult applyMembershipDiscount() {
        if (discountApplied) {
            return DiscountResult.ALREADY_APPLIED;
        }
        if (paymentStarted) {
            return DiscountResult.PAYMENT_STARTED;
        }
        if (!customer.isMember()) {
            return DiscountResult.NOT_A_MEMBER;
        }
        long discountCents =
                (amountDueCents * MEMBERSHIP_DISCOUNT_PERCENT + 50) / 100;
        amountDueCents -= discountCents;
        this.discountCents = discountCents;
        discountApplied = true;
        return DiscountResult.APPLIED;
    }

    /**
     * Whether the membership discount has been applied to this occasion.
     *
     * @return true once a discount was applied
     */
    public boolean isDiscountApplied() {
        return discountApplied;
    }

    /**
     * Pays part (or all) of the amount due by transferring from the card's
     * linked account to the store account (spec &sect;4.13).
     *
     * @param card        the customer's card to charge
     * @param amountCents amount to transfer, at most the remaining due
     * @return the outcome; nothing is transferred unless SUCCESS
     */
    public PaymentResult pay(PaymentCard card, long amountCents) {
        if (amountCents > amountDueCents) {
            return PaymentResult.EXCEEDS_AMOUNT_DUE;
        }
        boolean transferred = card.pay((int) amountCents, storeBankAccount);
        if (!transferred) {
            return PaymentResult.INSUFFICIENT_BALANCE;
        }
        amountDueCents -= amountCents;
        paidCents += amountCents;
        paymentStarted = true;
        return PaymentResult.SUCCESS;
    }

    /**
     * Whether the whole amount due has been paid by card.
     *
     * @return true when nothing is left to pay
     */
    public boolean isPaymentComplete() {
        return paymentStarted && amountDueCents == 0;
    }

    /**
     * Whether the basket of this occasion has been scanned.
     *
     * @return true once scanAll completed
     */
    public boolean isScanned() {
        return scanned;
    }
}

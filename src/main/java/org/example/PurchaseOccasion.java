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
                        item.getFullDescription(), runningTotal));
            } else {
                Integer index = groupIndexByName.get(item.getName());
                runningTotal += item.getInclusivePrice();
                if (index == null) {
                    lines.add(new ScanLine("1", item.getFullDescription(),
                            runningTotal));
                    groupIndexByName.put(item.getName(), lines.size() - 1);
                } else {
                    ScanLine line = lines.get(index);
                    int count = Integer.parseInt(line.getQuantity()) + 1;
                    lines.set(index, new ScanLine(String.valueOf(count),
                            line.getDescription(), runningTotal));
                }
            }
        }
        scanned = true;
        amountDueCents = runningTotal;
        return lines;
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
        if (!customer.isMember()) {
            return DiscountResult.NOT_A_MEMBER;
        }
        long discountCents =
                (amountDueCents * MEMBERSHIP_DISCOUNT_PERCENT + 50) / 100;
        amountDueCents -= discountCents;
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
     * Whether the basket of this occasion has been scanned.
     *
     * @return true once scanAll completed
     */
    public boolean isScanned() {
        return scanned;
    }
}

package org.example;

import java.util.Locale;
import java.util.Random;

/**
 * The command session: the state machine and command dispatch of
 * specs/pos-system.md &sect;&sect;2-4, free of any console I/O. Commands are
 * fed via {@link #execute(String)} and answered with the exact output text.
 */
public class PosSession {

    private static final String UNKNOWN_COMMAND =
            "Unknown command. Type 'help'.";

    private static final String[] CARD_ISSUERS = {"MasterCard", "Visa",
            "Amex"};
    private static final long DEFAULT_CARD_BALANCE_CENTS = 50000;

    private final ArticleRegistry registry;
    private final Random random;
    private boolean running = true;
    private SessionState state = SessionState.BOOT;
    private Store store;
    private Customer customer;
    private Basket basket;
    private PurchaseOccasion occasion;
    private int memberCounter;
    private int cardCounter;

    /** Where the flow currently is (spec §3). */
    private enum SessionState {
        BOOT, IN_STORE, AT_REGISTER, DONE
    }

    public PosSession(ArticleRegistry registry, Random random) {
        this.registry = registry;
        this.random = random;
    }

    /**
     * Runs one command line and produces its output.
     *
     * @param line raw user input
     * @return the response text to print
     */
    public String execute(String line) {
        String command = line.trim().toLowerCase(Locale.ROOT);
        switch (command) {
            case "exit":
                running = false;
                return "Goodbye!";
            case "help":
                return helpText();
            case "enter store":
                return enterStore();
            case "create basket":
                return createBasket();
            case "list store-items":
                return listStoreItems();
            case "to pos":
            case "to cash-register":
                return goToRegister();
            case "scan items":
                return scanItems();
            case "apply discount":
                return applyDiscount();
            case "payment":
                return payment();
            default:
                if (command.startsWith("add ")) {
                    return addItem(command.substring(4).trim());
                }
                if (command.startsWith("remove ")) {
                    return removeItem(command.substring(7).trim());
                }
                if (command.equals("list basket-items")) {
                    return listBasketItems();
                }
                if (command.equals("create card")
                        || command.startsWith("create card ")) {
                    return createCard(command.substring("create card".length())
                            .trim());
                }
                if (command.equals("use card")
                        || command.startsWith("use card ")) {
                    return useCard(command.substring("use card".length())
                            .trim());
                }
                return UNKNOWN_COMMAND;
        }
    }

    private String enterStore() {
        store = new Store("Kwik E-Smart", new Account(0));
        customer = new Customer("Customer", "");
        basket = null;
        state = SessionState.IN_STORE;
        return "Welcome to Kwik E-Smart! Go ahead and pick your items.";
    }

    private String createBasket() {
        if (state != SessionState.IN_STORE) {
            return "You must enter the store first ('enter store').";
        }
        basket = new Basket();
        return "Basket has been created";
    }

    private String listStoreItems() {
        if (state != SessionState.IN_STORE) {
            return "You must enter the store first ('enter store').";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-28s %-9s %s%n",
                "Name", "Full Description", "Unit", "Price"));
        for (Item item : registry.getAllArticles()) {
            String unit = item instanceof WeighedItem ? "per kg" : "1 pc";
            sb.append(String.format("%-15s %-28s %-9s %s%n",
                    item.getName(), item.getFullDescription(), unit,
                    PriceFormatter.format(item.getInclusivePrice())));
        }
        return sb.toString().stripTrailing();
    }

    private String goToRegister() {
        if (state != SessionState.IN_STORE) {
            return "You must enter the store first ('enter store').";
        }
        if (basket == null) {
            return "Create a basket first ('create basket').";
        }
        customer.goToRegister();
        occasion = new PurchaseOccasion(new BarcodeScanner(registry), basket,
                customer, store.getBankAccount());
        state = SessionState.AT_REGISTER;
        return "At store register. You can now scan the items or "
                + "register membership";
    }

    private String scanItems() {
        if (state != SessionState.AT_REGISTER) {
            return "Go to the cash register first ('to pos').";
        }
        if (occasion.isScanned()) {
            return "All items have already been scanned.";
        }
        if (basket.getItems().isEmpty()) {
            return "Your basket is empty.";
        }
        StringBuilder sb = new StringBuilder();
        for (ScanLine line : occasion.scanAll()) {
            sb.append(String.format("%-32s%s%n",
                    line.getQuantity() + " " + line.getDescription()
                            + " scanned",
                    "Total: " + PriceFormatter.format(
                            line.getRunningTotalCents())));
        }
        sb.append("All items scanned. Go to \"payment\" or \"apply discount\"");
        return sb.toString();
    }

    private String createCard(String amountArgument) {
        if (customer == null) {
            return "You must enter the store first ('enter store').";
        }
        long balanceCents = DEFAULT_CARD_BALANCE_CENTS;
        if (!amountArgument.isEmpty()) {
            Long parsed = parseAmountCents(amountArgument);
            if (parsed == null || parsed <= 0) {
                return "Amount must be a positive sum, e.g. 'create card "
                        + "300' or 'create card 29.95'.";
            }
            balanceCents = parsed;
        }
        cardCounter++;
        String issuer = CARD_ISSUERS[random.nextInt(CARD_ISSUERS.length)];
        customer.addCard(new PaymentCard(cardCounter, issuer,
                new Account((int) balanceCents)));
        return "A new " + issuer + " card has been created with id "
                + cardCounter + ". Balance: "
                + PriceFormatter.format(balanceCents);
    }

    /**
     * Parses a SEK amount like {@code 300} or {@code 29.95} into cents.
     *
     * @param text raw amount argument
     * @return the amount in cents, or null when malformed
     */
    private static Long parseAmountCents(String text) {
        String[] parts = text.trim().split("\\.");
        if (parts.length > 2 || parts[0].isEmpty()) {
            return null;
        }
        try {
            long kronor = Long.parseLong(parts[0]);
            long cents = 0;
            if (parts.length == 2) {
                if (parts[1].length() > 2 || parts[1].isEmpty()) {
                    return null;
                }
                String padded = parts[1].length() == 1
                        ? parts[1] + "0" : parts[1];
                cents = Long.parseLong(padded);
            }
            return kronor * 100 + cents;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String payment() {
        if (state == SessionState.DONE) {
            return "This purchase is already paid ('enter store' for a new "
                    + "visit).";
        }
        if (state != SessionState.AT_REGISTER) {
            return "Go to the cash register first ('to pos').";
        }
        if (!occasion.isScanned()) {
            return "Scan the items first ('scan items').";
        }
        if (basket.getItems().isEmpty()) {
            return "Your basket is empty.";
        }
        if (customer.getCards().isEmpty()) {
            return "You have no payment card. Use 'create card'.";
        }
        StringBuilder sb = new StringBuilder("Use card");
        for (PaymentCard card : customer.getCards()) {
            sb.append(String.format("%n%d %s (balance: %s)",
                    card.getCardId(), card.getIssuer(),
                    PriceFormatter.format(card.getBalance())));
        }
        sb.append(String.format("%nAmount due: %s",
                PriceFormatter.format(occasion.getAmountDueCents())));
        return sb.toString();
    }

    private String useCard(String argument) {
        if (state == SessionState.DONE) {
            return "This purchase is already paid ('enter store' for a new "
                    + "visit).";
        }
        if (state != SessionState.AT_REGISTER) {
            return "Go to the cash register first ('to pos').";
        }
        if (!occasion.isScanned()) {
            return "Scan the items first ('scan items').";
        }
        if (basket.getItems().isEmpty()) {
            return "Your basket is empty.";
        }
        String[] parts = argument.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return "Usage: use card <id> [amount]";
        }
        int cardId;
        try {
            cardId = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return "Usage: use card <id> [amount]";
        }
        PaymentCard card = findCard(cardId);
        if (card == null) {
            return "You have no card with that id. Use 'payment' to list "
                    + "them.";
        }
        long amountCents = occasion.getAmountDueCents();
        if (parts.length > 1) {
            Long parsed = parseAmountCents(parts[1]);
            if (parsed == null || parsed <= 0) {
                return "Amount must be a positive sum, e.g. 'use card 1 "
                        + "29.95'.";
            }
            amountCents = parsed;
        }
        PaymentResult result = occasion.pay(card, amountCents);
        switch (result) {
            case SUCCESS:
                if (!occasion.isPaymentComplete()) {
                    return "Your card has enough balance";
                }
                state = SessionState.DONE;
                return "Your card has enough balance\n"
                        + "Payment completed. Thank you for shopping at "
                        + store.getName() + "!\n"
                        + renderReceipt();
            case INSUFFICIENT_BALANCE:
                return "Your card has insufficient balance";
            default:
                return "That is more than the remaining "
                        + PriceFormatter.format(occasion.getAmountDueCents())
                        + " due.";
        }
    }

    private String renderReceipt() {
        return new Receipt(store.getName(), occasion.getScanLines(),
                occasion.getDiscountCents(), occasion.getPaidCents()).print();
    }

    private PaymentCard findCard(int cardId) {
        for (PaymentCard card : customer.getCards()) {
            if (card.getCardId() == cardId) {
                return card;
            }
        }
        return null;
    }

    private String applyDiscount() {
        if (state != SessionState.AT_REGISTER) {
            return "Go to the cash register first ('to pos').";
        }
        if (!occasion.isScanned()) {
            return "Scan the items first ('scan items').";
        }
        DiscountResult result = occasion.applyMembershipDiscount();
        switch (result) {
            case APPLIED:
                return "Discount has succeeded. Enjoy 20% off your purchase!";
            case NOT_A_MEMBER:
                return "Applying discount failed. You need to be a member";
            case ALREADY_APPLIED:
                return "A discount has already been applied to this purchase.";
            default:
                return "Discount can no longer be applied once payment "
                        + "has started.";
        }
    }

    private String basketStateHint() {
        if (state == SessionState.AT_REGISTER) {
            return "Basket commands are not available at the register.";
        }
        return "You must enter the store first ('enter store').";
    }

    private String addItem(String argument) {
        if (state != SessionState.IN_STORE) {
            return basketStateHint();
        }
        if (basket == null) {
            return "Create a basket first ('create basket').";
        }
        String[] parts = argument.split("\\s+");
        Item template = registry.findByName(parts[0]);
        if (template == null) {
            return "Unknown item. Use 'list store-items'.";
        }
        if (template instanceof WeighedItem) {
            return addWeighed(template, parts);
        }
        if (parts.length > 1) {
            return "That article is sold per piece; use 'add " + parts[0]
                    + "' without a quantity.";
        }
        basket.addItem(copyOf(template, 1));
        return "1 " + template.getFullDescription() + " added to basket";
    }

    private String addWeighed(Item template, String[] parts) {
        if (parts.length < 2) {
            return "That article is sold by weight; give grams, e.g. 'add "
                    + template.getName() + " 200'.";
        }
        int grams;
        try {
            grams = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Weight must be a whole number of grams.";
        }
        if (grams <= 0) {
            return "Weight must be positive grams.";
        }
        basket.addItem(copyOf(template, grams));
        return grams + "g " + template.getFullDescription()
                + " added to basket";
    }

    private Item copyOf(Item template, int grams) {
        if (template instanceof WeighedItem) {
            // Registry weighed templates are seeded at 1000 g, so their
            // line price is exactly the per-kg base price.
            return new WeighedItem(template.getName(), template.getPrice(),
                    template.getVatRate(), grams,
                    template.getArticleNumber(),
                    template.getFullDescription());
        }
        return template;
    }

    private String removeItem(String handle) {
        if (state != SessionState.IN_STORE) {
            return basketStateHint();
        }
        if (basket == null) {
            return "Create a basket first ('create basket').";
        }
        Item removed = basket.removeOne(handle);
        if (removed == null) {
            return "That item is not in your basket.";
        }
        return "One " + removed.getFullDescription()
                + " has been removed from basket";
    }

    private String listBasketItems() {
        if (state != SessionState.IN_STORE) {
            return basketStateHint();
        }
        if (basket == null) {
            return "Create a basket first ('create basket').";
        }
        if (basket.getItems().isEmpty()) {
            return "Your basket is empty.";
        }
        StringBuilder sb = new StringBuilder("The basket has the following items:");
        for (BasketLine line : basket.lines()) {
            sb.append(String.format("%n%-8s%s", line.getQuantity(),
                    line.getDescription()));
        }
        return sb.toString();
    }

    /**
     * Whether the session accepts more commands (false after exit).
     *
     * @return true while running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * The current customer, for the console shell and tests.
     *
     * @return the customer of this visit, null before entering the store
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * The current store, for the console shell and tests.
     *
     * @return the store of this visit, null before entering the store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Reason why membership registration cannot start right now, checked
     * before prompting for input (spec &sect;4.11).
     *
     * @return refusal text, or null when registration may proceed
     */
    public String membershipRegistrationRefusal() {
        if (state == SessionState.BOOT || state == SessionState.DONE) {
            return "You must enter the store first ('enter store').";
        }
        if (state != SessionState.AT_REGISTER) {
            return "Membership registration is only available at the cash "
                    + "register ('to pos').";
        }
        if (customer.isMember()) {
            return "You are already a member.";
        }
        return null;
    }

    /**
     * Completes a membership registration with the entered data
     * (spec &sect;4.11).
     *
     * @param name         the entered name (recorded on the visit)
     * @param birthdayInput birthday as yyyymmdd
     * @param personnr     the person number, any non-empty text
     * @return confirmation with member number, or an error message
     */
    public String registerMembership(String name, String birthdayInput,
                                     String personnr) {
        String refusal = membershipRegistrationRefusal();
        if (refusal != null) {
            return refusal;
        }
        java.time.LocalDate birthday;
        try {
            birthday = java.time.LocalDate.parse(birthdayInput.trim(),
                    java.time.format.DateTimeFormatter.BASIC_ISO_DATE
                            .withResolverStyle(java.time.format
                                    .ResolverStyle.STRICT));
        } catch (java.time.format.DateTimeParseException e) {
            return "Invalid birthday format. Use yyyymmdd.";
        }
        if (personnr == null || personnr.isBlank()) {
            return "Person number must not be empty.";
        }
        memberCounter++;
        String memberNumber = String.format("M%06d", memberCounter);
        customer.joinMembership(birthday, memberNumber);
        return "Congratulations! You can now use discounts. Your member "
                + "number is " + memberNumber + "!";
    }

    private String helpText() {
        return "Available commands:\n"
                + "enter store         - enter the shop as a customer\n"
                + "run pos             - automatic demo flow with random setup\n"
                + "create basket       - start an empty shopping basket\n"
                + "list store-items    - show the assortment\n"
                + "add <item> [qty]    - put an article in the basket\n"
                + "remove <item>       - take one article out of the basket\n"
                + "list basket-items   - show your basket\n"
                + "to pos              - go to the cash register\n"
                + "to cash-register    - same as 'to pos'\n"
                + "scan items          - let the cashier scan your basket\n"
                + "apply discount      - 20% membership discount\n"
                + "register membership - join the club at the register\n"
                + "payment             - show cards and amount due\n"
                + "use card <id> [amt] - pay by card, fully or partially\n"
                + "create card [amt]   - issue a new payment card\n"
                + "help                - show this list\n"
                + "exit                - leave the program";
    }
}

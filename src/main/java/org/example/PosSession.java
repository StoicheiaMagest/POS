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

    private final ArticleRegistry registry;
    private final Random random;
    private boolean running = true;
    private SessionState state = SessionState.BOOT;
    private Store store;
    private Customer customer;
    private Basket basket;

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

    private String addItem(String argument) {
        if (state != SessionState.IN_STORE) {
            return "You must enter the store first ('enter store').";
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
            return "You must enter the store first ('enter store').";
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
            return "You must enter the store first ('enter store').";
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

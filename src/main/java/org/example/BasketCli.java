package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;

// Line-based command interface for trying out the basket and receipt code.
// Input and output are injected so the commands can be tested with scripted input.
public class BasketCli {

    private final ArticleRegistry catalog;
    private final BufferedReader in;
    private final PrintStream out;
    private final Basket basket = new Basket();

    public BasketCli(ArticleRegistry catalog, Reader in, PrintStream out) {
        this.catalog = catalog;
        this.in = new BufferedReader(in);
        this.out = out;
    }

    public Basket getBasket() {
        return basket;
    }

    public void run() {
        printHelp();
        try {
            String line;
            out.print("> ");
            while ((line = in.readLine()) != null) {
                if (!line.isBlank() && !execute(line.trim().split("\\s+"))) {
                    break;
                }
                out.print("> ");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        out.println("Bye");
    }

    // Returns false when the loop should stop.
    private boolean execute(String[] parts) {
        String command = parts[0].toLowerCase();
        switch (command) {
            case "quit":
                return false;
            case "help":
                printHelp();
                return true;
            case "catalog":
                printCatalog();
                return true;
            case "add":
                add(parts);
                return true;
            case "list":
                list();
                return true;
            case "remove":
                remove(parts);
                return true;
            default:
                out.println("Unknown command: " + parts[0]);
                return true;
        }
    }

    private void add(String[] parts) {
        if (parts.length < 2 || parts.length > 3) {
            out.println("Usage: add <article> [qty|grams]");
            return;
        }
        try {
            int articleNumber = parseNumber(parts[1]);
            Item item = catalog.findByArticleNumber(articleNumber);
            if (item == null) {
                out.println("No such article: " + articleNumber);
                return;
            }
            Integer amount = parts.length == 3 ? parseNumber(parts[2]) : null;
            if (item instanceof WeighedItem weighed) {
                addWeighed(weighed, amount);
            } else {
                addUnits(item, amount == null ? 1 : amount);
            }
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
        }
    }

    private void addUnits(Item item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        for (int i = 0; i < quantity; i++) {
            basket.addItem(item);
        }
        out.println("Added " + quantity + " x " + item.getName());
    }

    // The catalog holds weighed items as templates; each basket entry gets its own weight.
    private void addWeighed(WeighedItem template, Integer grams) {
        if (grams == null) {
            throw new IllegalArgumentException(
                    template.getName() + " is sold by weight, give the weight in grams");
        }
        if (grams <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        basket.addItem(new WeighedItem(template.getName(), template.getPricePerKg(),
                template.getVatRate(), grams, template.getArticleNumber()));
        out.println("Added " + template.getName() + " " + Money.formatDecimal(grams, 3, ',') + " kg");
    }

    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number: " + text);
        }
    }

    private void remove(String[] parts) {
        if (parts.length != 2) {
            out.println("Usage: remove <no>");
            return;
        }
        try {
            Item item = itemNumber(parts[1]);
            basket.removeItem(item);
            out.println("Removed " + item.getName());
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
        }
    }

    // Looks up a basket item by its 1-based number as shown by the list command.
    private Item itemNumber(String text) {
        int number = parseNumber(text);
        List<Item> items = basket.getItems();
        if (number < 1 || number > items.size()) {
            throw new IllegalArgumentException("No item number " + number + " in the basket");
        }
        return items.get(number - 1);
    }

    private void list() {
        if (basket.getItems().isEmpty()) {
            out.println("Basket is empty");
            return;
        }
        int number = 1;
        for (Item item : basket.getItems()) {
            String weight = item instanceof WeighedItem weighed
                    ? Money.formatDecimal(weighed.getWeight(), 3, ',') + " kg  "
                    : "";
            out.println(number++ + "  " + item.getName() + "  " + weight
                    + Money.format(item.getInclusivePrice(), ',') + " SEK");
        }
        out.println("Total: " + Money.format(basket.getTotalPrice(), ',') + " SEK");
    }

    private void printCatalog() {
        for (Item item : catalog.findAll()) {
            String price = item instanceof WeighedItem weighed
                    ? Money.format(weighed.getPricePerKg(), ',') + " SEK/kg"
                    : Money.format(item.getInclusivePrice(), ',') + " SEK";
            out.println(item.getArticleNumber() + "  " + item.getName() + "  " + price);
        }
    }

    private void printHelp() {
        out.println("Commands:");
        out.println("  catalog                     show the items you can pick from");
        out.println("  add <article> [qty|grams]   add an item (quantity, or grams for weighed items)");
        out.println("  list                        show the basket");
        out.println("  update <no> <grams>         change the weight of a weighed item in the basket");
        out.println("  remove <no>                 remove an item from the basket");
        out.println("  receipt                     print the receipt");
        out.println("  quit                        exit");
    }
}

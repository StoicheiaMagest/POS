package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;

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
            default:
                out.println("Unknown command: " + parts[0]);
                return true;
        }
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

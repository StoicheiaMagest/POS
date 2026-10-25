package org.example;

import java.time.LocalDateTime;
import java.util.Scanner;

public class RegisterSystem2 {
    private static final String STORE_NAME = "Java Butiken";
    Scanner input = new Scanner(System.in);
    private static final String NEW_ORDER_COMMAND = "NEW";
    private static final String SCAN_COMMAND = "SCAN";
    private static final String WEIGH_COMMAND = "WEIGH";
    private static final String REMOVE_COMMAND = "REMOVE";
    private static final String PAY_COMMAND = "PAY";
    private static final String EXIT_COMMAND = "EXIT";
    private String command;

    private final ArticleRegistry registry;
    private final BarcodeScanner scanner;
    private final Scale scale = new Scale();
    private final Discount discount = new Discount();
    private final Account storeBankAccount;

    private Basket basket;
    private Customer customer;
    private Account customerAccount;
    private int nextReceiptNo = 1;

    // Demo setup, also used by tests.
    public RegisterSystem2() {
        this(createDemoRegistry(), new Account(0));
    }

    public RegisterSystem2(ArticleRegistry registry, Account storeBankAccount) {
        this.registry = registry;
        this.scanner = new BarcodeScanner(registry);
        this.storeBankAccount = storeBankAccount;
    }

    public static ArticleRegistry createDemoRegistry() {
        ArticleRegistry registry = new ArticleRegistry();
        registry.addItem(111, new UnitPricedArticle("Milk", 400, Vat.FOOD, 111));   // 3 for 2
        registry.addItem(222, new UnitPricedArticle("Bread", 150, Vat.FOOD, 222));  // 2 kr off
        registry.addItem(333, new WeighedItem("Apple", 3000, Vat.FOOD, 0, 333));    // template
        return registry;
    }

    public static void main(String[] args) {
        new RegisterSystem2().start();
    }

    private void start() {
        setUp();
        runCommandLoop();
        shutDown();
    }

    private void setUp() {
        System.out.println("Register starting");
        System.out.println("The following commands are availible:");
        printCommands();
    }

    private void printCommands() {
        System.out.println("* NEW [MEMBER|SENIOR] - start a new order, optionally a member or senior customer");
        System.out.println("* SCAN <article number> - add an item to the basket");
        System.out.println("* WEIGH <article number> <grams> - weigh and add an item");
        System.out.println("* REMOVE <article number> - remove the first item with that number");
        System.out.println("* PAY - pay with card and print the receipt");
        System.out.println("* EXIT");
    }

    private void runCommandLoop() {
        do {
            command = input.nextLine().toUpperCase();
            executeCommand(command);
            System.out.println();
        } while (!command.equalsIgnoreCase(EXIT_COMMAND));
    }

    private void executeCommand(String command) {
        String[] parts = command.split("\\s+");
        switch (parts[0]) {
            case NEW_ORDER_COMMAND:
                commandNewOrder(parts);
                break;

            case SCAN_COMMAND:
                commandScan(parts);
                break;

            case WEIGH_COMMAND:
                commandWeigh(parts);
                break;

            case REMOVE_COMMAND:
                commandRemove(parts);
                break;

            case PAY_COMMAND:
                commandPay();
                break;

            case EXIT_COMMAND:
                break;
            default:
                System.out.println("Error, try again!");
        }
    }

    private void commandNewOrder(String[] parts) {
        basket = new Basket();
        customer = createCustomer(parts);
        customerAccount = new Account(100000); // demo account, no bank integration
        System.out.println("New order started for "
                + (customer == null ? "an anonymous customer" : customer.getName()));
    }

    private Customer createCustomer(String[] parts) {
        if (parts.length > 1 && parts[1].equals("MEMBER")) {
            return new Customer("Member", "19900101-1234", true, false);
        }
        if (parts.length > 1 && parts[1].equals("SENIOR")) {
            return new Customer("Senior", "19500101-1234", true, true);
        }
        return null;
    }

    private void commandScan(String[] parts) {
        if (basket == null || parts.length < 2) {
            System.out.println("Error, start an order with NEW first!");
            return;
        }
        try {
            basket.addItem(scanner.scan(Integer.parseInt(parts[1])));
            System.out.println("Scanned " + parts[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Error, " + e.getMessage());
        }
    }

    private void commandWeigh(String[] parts) {
        if (basket == null || parts.length < 3) {
            System.out.println("Error, start an order with NEW first!");
            return;
        }
        try {
            Item item = registry.findByArticleNumber(Integer.parseInt(parts[1]));
            if (!(item instanceof WeighedItem weighed)) {
                System.out.println("Error, article " + parts[1] + " is not a weighed item!");
                return;
            }
            basket.addItem(scale.weigh(weighed, Integer.parseInt(parts[2])));
            System.out.println("Weighed " + parts[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Error, could not weigh the item!");
        }
    }

    private void commandRemove(String[] parts) {
        if (basket == null || parts.length < 2) {
            System.out.println("Error, start an order with NEW first!");
            return;
        }
        try {
            int articleNumber = Integer.parseInt(parts[1]);
            for (Item item : basket.getItems()) {
                if (item.getArticleNumber() == articleNumber) {
                    basket.removeItem(item);
                    System.out.println("Removed " + item.getName());
                    return;
                }
            }
            System.out.println("Error, no such item in the basket!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error, " + e.getMessage());
        }
    }

    private void commandPay() {
        if (basket == null) {
            System.out.println("Error, start an order with NEW first!");
            return;
        }
        PurchaseOccasion occasion =
                new PurchaseOccasion(basket, customer, storeBankAccount, discount);
        if (!occasion.pay(new Card(customerAccount))) {
            System.out.println("Error, payment failed!");
            return;
        }
        Receipt receipt =
                new Receipt(nextReceiptNo++, LocalDateTime.now(), STORE_NAME, occasion);
        System.out.println(receipt.print());
    }

    private void shutDown() {
        System.out.println("Register shutting down");
        input.close();
    }
}

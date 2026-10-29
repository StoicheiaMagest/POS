
package org.example;

import java.util.Scanner;
import java.util.function.Consumer;

public class RegisterSystem {

    private final Scanner input;

    private static final String NEW_ORDER_COMMAND = "SCAN ITEM";
    private static final String DEMO_COMMAND = "DEMO";
    private static final String EXIT_COMMAND = "EXIT";

    // Optional, only set when started from the demo launcher. Null in production.
    // It gets the register's Scanner so a demo menu can read input without opening a second Scanner on System.in.
    private final Consumer<Scanner> demo;

    private String command;

    public RegisterSystem() {
        this(new Scanner(System.in));
    }

    RegisterSystem(Scanner input) {
        this(input, null);
    }

    RegisterSystem(Scanner input, Consumer<Scanner> demo) {
        this.input = input;
        this.demo = demo;
    }

    public static void main(String[] args) {
        new RegisterSystem().start();
    }

    void start() {
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
        if (demo != null) {
            System.out.println("* Demo");
        }
        System.out.println("* Exit");
    }

    private void runCommandLoop() {
        do {
            command = input.nextLine().toUpperCase();
            executeCommand(command);
            System.out.println();
        } while (!command.equalsIgnoreCase(EXIT_COMMAND));
    }

    private void executeCommand(String command) {
        switch (command) {
            case NEW_ORDER_COMMAND:
                commandNewOrder();
                break;

            case DEMO_COMMAND:
                if (demo == null) {
                    System.out.println("Error, try again!");
                } else {
                    demo.accept(input);
                }
                break;

            case EXIT_COMMAND:
                break;

            default:
                System.out.println("Error, try again!");
        }
    }

    private void commandNewOrder() {
        // TODO: Replace void with Order when that class is created.
        // Scan an item or weigh an item.
        // Add the item to the basket.
    }

    private void shutDown() {
        System.out.println("Register shutting down");
        input.close();
    }
}

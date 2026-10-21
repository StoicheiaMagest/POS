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
            default:
                return UNKNOWN_COMMAND;
        }
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

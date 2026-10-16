package org.example;

import java.util.Scanner;

Scanner input = new Scanner(System.in);
private static final String EXIT_COMMAND = "EXIT";
private String command;

public class Register {

    public static void main(String[] args) {
        new Register().start();
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
        System.out.println("* Exit");
    }

    private void runCommandLoop() {
        do { 
            readCommand();
            executeCommand(command);
            System.out.println();
        } while (!command.equalsIgnoreCase(EXIT_COMMAND));
    }

    private void readCommand() {
        command = input.nextLine().toUpperCase();
    }

    private void executeCommand(String command) {
        switch (command) {
            case EXIT_COMMAND:
                break;
            default:
                System.out.println("Error, try again!");
        }
    }


    private void shutDown() {
        System.out.println("Register shutting down");
        input.closeInput();
    }
}
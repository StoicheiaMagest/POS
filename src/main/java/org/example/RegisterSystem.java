package org.example;

import java.util.Scanner;

public class RegisterSystem {
    Scanner input = new Scanner(System.in);
    private static final String NEW_ORDER_COMMAND = "SCAN ITEM";
    private static final String EXIT_COMMAND = "EXIT";
    private String command;

    public static void main(String[] args) {
        new RegisterSystem().start();
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

            case EXIT_COMMAND:
                break;
            default:
                System.out.println("Error, try again!");
        }
    }

    private void commandNewOrder() {//byt void till order när den klassen är skapad
        //loop
            //scanna vara eller väg vara
            //det läggs till i basket
            
    }




    private void shutDown() {
        System.out.println("Register shutting down");
        input.close();
    }
}
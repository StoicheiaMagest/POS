package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Not closed: closing it would also close System.in, which the register systems still need
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Choose register system:");
            System.out.println("1. RegisterSystem");
            System.out.println("2. RegisterSystem2");
            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                RegisterSystem.main(args);
                return;
            } else if (choice.equals("2")) {
                RegisterSystem2.main(args);
                return;
            }
            System.out.println("Invalid choice, enter 1 or 2.");
        }
    }
}
